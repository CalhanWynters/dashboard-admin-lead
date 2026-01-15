package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.money;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.validationchecks.DomainGuard;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * A Value Object representing a specific amount of money in a specific currency.
 * Uses BigDecimal for precision and immutability, aligned with DomainGuard for 2026 standards.
 */
record Money(BigDecimal amount, Currency currency, int precision, RoundingMode roundingMode)
        implements Comparable<Money> {

    /**
     * Ensures the internal amount is always scaled correctly upon creation,
     * regardless of the input BigDecimal's original scale.
     */
    public Money {
        // Use DomainGuard for standardized null checks (Throws VAL-001)
        DomainGuard.notNull(amount, "Amount");
        DomainGuard.notNull(currency, "Currency");
        DomainGuard.notNull(roundingMode, "Rounding Mode");

        // Use DomainGuard for standardized range check (Throws VAL-007)
        DomainGuard.nonNegative(BigDecimal.valueOf(precision), "Precision");

        // This is crucial: we ensure the internal 'amount' field value is forced to the correct scale/mode.
        amount = amount.setScale(precision, roundingMode);
    }

    /**
     * Constructor overload for common defaults (e.g., USD usually has 2 decimals).
     * Defaults to 2 decimal places and HALF_UP rounding.
     */
    public Money(BigDecimal amount, Currency currency) {
        this(amount, currency, 2, RoundingMode.HALF_UP);
    }

    // --- Factory Methods (Remain as is) ---

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public static Money of(BigDecimal amount, String currencyCode) {
        return new Money(amount, Currency.getInstance(currencyCode));
    }


    // --- DDD Operations (using DomainGuard for consistency checks) ---

    public Money add(Money other) {
        // Use DomainGuard.ensure for cross-field integrity checks (VAL-MONEY-001)
        DomainGuard.ensure(
                this.currency.equals(other.currency) && this.precision == other.precision,
                "Cannot add money objects with different currencies or precision settings.",
                "VAL-MONEY-001",
                "CURRENCY_MISMATCH"
        );

        BigDecimal result = this.amount.add(other.amount);
        return new Money(result, this.currency, this.precision, this.roundingMode);
    }

    public Money multiply(int multiplier) {
        BigDecimal result = this.amount.multiply(BigDecimal.valueOf(multiplier));
        return new Money(result, this.currency, this.precision, this.roundingMode);
    }

    public Money divide(int divisor) {
        // Use DomainGuard for range checks/business rules related to input values (VAL-011)
        DomainGuard.positive(divisor, "Divisor");

        BigDecimal result = this.amount.divide(BigDecimal.valueOf(divisor), this.precision, this.roundingMode);
        return new Money(result, this.currency, this.precision, this.roundingMode);
    }

    // --- Comparison and Utility Methods ---

    @Override
    public int compareTo(Money other) {
        // Use DomainGuard.ensure for cross-field integrity checks (VAL-MONEY-002)
        DomainGuard.ensure(
                this.currency.equals(other.currency),
                "Cannot compare different currencies.",
                "VAL-MONEY-002",
                "CURRENCY_MISMATCH"
        );
        return this.amount.compareTo(other.amount);
    }

    public boolean isZero() {
        return this.amount.signum() == 0;
    }

    public boolean equalsValue(Money other) {
        // Use DomainGuard.notNull internally if preferred, but Objects.requireNonNull is fine in internal helper methods
        if (other == null) return false;
        boolean amountsEqual = this.amount.compareTo(other.amount) == 0;
        boolean currenciesEqual = this.currency.equals(other.currency);
        return amountsEqual && currenciesEqual;
    }

    @Override
    public String toString() {
        return String.format("%s %s", currency.getCurrencyCode(), amount.toPlainString());
    }
}

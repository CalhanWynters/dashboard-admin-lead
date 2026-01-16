package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.validationchecks.DomainGuard;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

/**
 * Hardened Price Value Object for Java 21/25 (2026 Edition).
 * Implements scale boundaries and currency consistency via DomainGuard.
 */
public record Price(BigDecimal price, int precision, Currency currency) {

    private static final int MAX_PRECISION = 10;
    private static final BigDecimal MAX_PRICE = new BigDecimal("100000000.00");

    /**
     * Create a PriceVO using a specific price and currency.
     */
    public Price(BigDecimal price, Currency currency) {
        this(price, DomainGuard.notNull(currency, "Currency").getDefaultFractionDigits(), currency);
    }

    /**
     * Flexible constructor for default USD pricing.
     */
    public Price(BigDecimal price) {
        this(price, Currency.getInstance("USD"));
    }

    /**
     * Compact Constructor enforcing financial invariants.
     */
    public Price {
        // 1. Existence
        DomainGuard.notNull(price, "Price");
        DomainGuard.notNull(currency, "Currency");

        // 2. Precision Range (Throws VAL-007)
        DomainGuard.range(precision, 0, MAX_PRECISION, "Precision");

        // 3. Financial Semantics (Throws VAL-005)
        DomainGuard.nonNegative(price, "Price");

        DomainGuard.ensure(
                price.compareTo(MAX_PRICE) <= 0,
                "Price exceeds maximum logical system boundary.",
                "VAL-007", "RANGE"
        );

        // 4. Currency Consistency Checks
        int minFractionDigits = currency.getDefaultFractionDigits();

        DomainGuard.ensure(
                precision >= minFractionDigits,
                "Explicit precision %d is insufficient for currency %s (minimum %d)"
                        .formatted(precision, currency.getCurrencyCode(), minFractionDigits),
                "VAL-011", "SEMANTICS"
        );

        int actualInputScale = price.stripTrailingZeros().scale();
        DomainGuard.ensure(
                actualInputScale <= minFractionDigits,
                "Input price scale (%d) exceeds currency %s allowed precision (%d)."
                        .formatted(actualInputScale, currency.getCurrencyCode(), minFractionDigits),
                "VAL-011", "SEMANTICS"
        );

        // 5. Normalization
        price = price.setScale(precision, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "%s %s".formatted(currency.getCurrencyCode(), price.toPlainString());
    }
}

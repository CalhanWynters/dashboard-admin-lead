package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Hardened Price Value Object for Java 25.
 * Implements strict scale boundaries and Cross-Field currency consistency.
 */


public record PriceVO(BigDecimal price, int precision, Currency currency) {

    // Boundary: Prevent Arithmetic DoS by limiting max decimal digits
    private static final int MAX_PRECISION = 10;
    // Boundary: Logical upper limit for a single product price ($100M)
    private static final BigDecimal MAX_PRICE = new BigDecimal("100000000.00");

    /**
     * Explicit constructor for creating a PriceVO with a specific price and currency,
     * using the default precision of that currency. The precision field in this case
     * becomes synonymous with the currency's default fraction digits.
     */
    public PriceVO(BigDecimal price, Currency currency) {
        this(price, currency.getDefaultFractionDigits(), currency);
    }

    /**
     * Flexible Constructor (JEP 513) for default USD pricing (uses the explicit constructor).
     */
    public PriceVO(BigDecimal price) {
        this(price, Currency.getInstance("USD"));
    }

    /**
     * Compact Constructor for Domain Validation and Normalization.
     */
    public PriceVO {
        // 1. Existence & Nullability
        Objects.requireNonNull(price, "Price cannot be null");
        Objects.requireNonNull(currency, "Currency must not be null");

        // 2. Size & Boundary (Prevention of Arithmetic DoS)
        if (precision < 0 || precision > MAX_PRECISION) {
            throw new IllegalArgumentException("Precision must be between 0 and %d".formatted(MAX_PRECISION));
        }

        // 3. Semantics: Range Validation
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (price.compareTo(MAX_PRICE) > 0) {
            throw new IllegalArgumentException("Price exceeds maximum logical system boundary.");
        }

        // 4. Cross-Field Consistency (Currency Precision Check)
        int minFractionDigits = currency.getDefaultFractionDigits();
        if (precision < minFractionDigits) {
            throw new IllegalArgumentException(
                    "Explicit precision %d is insufficient for currency %s (minimum %d)"
                            .formatted(precision, currency.getCurrencyCode(), minFractionDigits)
            );
        }

        // --- STRICT INPUT VALIDATION AGAINST CURRENCY PRECISION ---
        // Prevents fractional pennies ($10.123 USD is rejected, not rounded)
        int actualInputScale = price.stripTrailingZeros().scale();
        if (actualInputScale > minFractionDigits) {
            throw new IllegalArgumentException(
                    "Input price scale (%d) exceeds currency %s allowed precision (%d). Fractional pennies not allowed."
                            .formatted(actualInputScale, currency.getCurrencyCode(), minFractionDigits)
            );
        }
        // ----------------------------------------------------------------

        // 5. Normalization (Enforcing Canonical Form)
        // Normalizes the price using the specified precision.
        // This only rounds if the *provided* precision is higher than the *actual* input scale
        // (e.g., input $10.00 with precision 2). The previous validation ensures we don't truncate data.
        price = price.setScale(precision, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        // Using getCurrencyCode() is generally safer than getSymbol() for admin/logging purposes
        return "%s %s".formatted(currency.getCurrencyCode(), price.toPlainString());
    }
}

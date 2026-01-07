package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Hardened Price Value Object for Java 25.
 * Implements strict scale boundaries and ensures Cross-Field currency consistency.
 */
public record PriceVO(BigDecimal price, int precision, Currency currency) {

    private static final int MAX_PRECISION = 10; // Maximum allowed decimal digits
    private static final BigDecimal MAX_PRICE = new BigDecimal("100000000.00"); // Logical upper limit for product price

    /**
     * Create a PriceVO using a specific price and currency,
     * defaulting to the currency's default fraction digits for precision.
     */
    public PriceVO(BigDecimal price, Currency currency) {
        Objects.requireNonNull(currency, "Currency must not be null");
        this(price, currency.getDefaultFractionDigits(), currency); // Delegating to the compact constructor
    }

    /**
     * Flexible constructor for default USD pricing.
     */
    public PriceVO(BigDecimal price) {
        this(price, Currency.getInstance("USD")); // Default currency is USD
    }

    /**
     * Validates input data and normalizes price.
     */
    public PriceVO {
        Objects.requireNonNull(price, "Price cannot be null");
        Objects.requireNonNull(currency, "Currency must not be null");

        // Validate precision range
        if (precision < 0 || precision > MAX_PRECISION) {
            throw new IllegalArgumentException("Precision must be between 0 and %d".formatted(MAX_PRECISION));
        }

        // Validate price range
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (price.compareTo(MAX_PRICE) > 0) {
            throw new IllegalArgumentException("Price exceeds maximum logical system boundary.");
        }

        // Validate currency precision
        int minFractionDigits = currency.getDefaultFractionDigits();
        if (precision < minFractionDigits) {
            throw new IllegalArgumentException(
                    "Explicit precision %d is insufficient for currency %s (minimum %d)"
                            .formatted(precision, currency.getCurrencyCode(), minFractionDigits)
            );
        }

        // Validate input scale against currency precision
        int actualInputScale = price.stripTrailingZeros().scale();
        if (actualInputScale > minFractionDigits) {
            throw new IllegalArgumentException(
                    "Input price scale (%d) exceeds currency %s allowed precision (%d). Fractional pennies not allowed."
                            .formatted(actualInputScale, currency.getCurrencyCode(), minFractionDigits)
            );
        }

        // Normalize the price using the specified precision
        price = price.setScale(precision, RoundingMode.HALF_UP);
    }
    public Currency currency() {
        return currency; // Assuming this method is implicitly available from the record.
    }

    @Override
    public String toString() {
        return "%s %s".formatted(currency.getCurrencyCode(), price.toPlainString());
    }
}

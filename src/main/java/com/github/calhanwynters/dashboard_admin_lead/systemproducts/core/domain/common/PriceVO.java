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
     * Flexible Constructor (JEP 513) for default USD pricing.
     */
    public PriceVO(BigDecimal price) {
        this(price, Currency.getInstance("USD").getDefaultFractionDigits(), Currency.getInstance("USD"));
    }

    /**
     * Compact Constructor for Domain Validation.
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

        // 4. Cross-Field Consistency
        // In 2025, a VO must ensure provided precision is compatible with currency standards
        // or specifically tailored for the domain (e.g., fuel pricing with 3 decimals).
        int minFractionDigits = currency.getDefaultFractionDigits();
        if (precision < minFractionDigits) {
            throw new IllegalArgumentException(
                    "Precision %d is insufficient for currency %s (minimum %d)"
                            .formatted(precision, currency.getCurrencyCode(), minFractionDigits)
            );
        }

        // 5. Normalization (Enforcing Canonical Form)
        // Ensures .equals() works correctly between 10 and 10.00
        price = price.setScale(precision, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        // Standardized 2025 formatting
        return "%s %s".formatted(currency.getSymbol(), price.toPlainString());
    }
}

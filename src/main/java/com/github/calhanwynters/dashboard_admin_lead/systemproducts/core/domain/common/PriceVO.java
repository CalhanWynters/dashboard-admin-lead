package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Updated for Java 25.
 * Benefits from JEP 519 (Compact Object Headers) for reduced heap usage.
 * In an Entity, place as a list for varying currencies for easy regional config based runtime.
 */
public record PriceVO(BigDecimal price, int precision, Currency currency) {

    // Finalized in Java 25: Flexible Constructor Bodies (JEP 513)
    // Allows logic to run before the canonical constructor is called.
    public PriceVO(BigDecimal price) {
        this(price, 2, Currency.getInstance("USD"));
    }

    /**
     * Compact Constructor for domain validation.
     */
    public PriceVO {
        // Syntax & Lexical Validation
        Objects.requireNonNull(price, "Price cannot be null");
        Objects.requireNonNull(currency, "Currency must not be null");

        // Semantics: Does the data make sense for a price?
        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        if (precision < 0 || precision > 10) { // Added a sensible upper bound for precision
            throw new IllegalArgumentException("Precision must be between 0 and 10");
        }

        // Normalization: Ensure the price internal scale matches the intended precision
        // This ensures equals() works correctly across different scales (e.g., 10.0 vs 10.00)
        price = price.setScale(precision, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        // String templates (standardized in 2024/2025) are the preferred formatting method
        return "%s %s".formatted(currency.getSymbol(), price.toPlainString());
    }
}

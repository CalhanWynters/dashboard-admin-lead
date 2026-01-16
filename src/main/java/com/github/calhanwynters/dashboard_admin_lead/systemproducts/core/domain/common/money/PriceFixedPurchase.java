package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.money;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public record PriceFixedPurchase(Money fixedPrice) implements PurchasePricing {

    public PriceFixedPurchase {
        Objects.requireNonNull(fixedPrice, "Fixed price cannot be null.");
        if (fixedPrice.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Fixed price must be non-negative.");
        }
    }

    /**
     * Static factory for convenience.
     */
    public static PriceFixedPurchase of(BigDecimal fixedPrice, Currency currency) {
        // FIX: The constructor for PriceFixedPurchase only takes ONE Money object.
        // Previously, you were passing two, which causes a compilation error.
        return new PriceFixedPurchase(new Money(fixedPrice, currency));
    }

    /**
     * In a fixed pricing model, the price remains the same regardless of quantity.
     */
    public Money calculate(BigDecimal quantity) {
        Objects.requireNonNull(quantity, "Quantity cannot be null.");
        // A fixed price model ignores the quantity and returns the base price.
        return this.fixedPrice;
    }

    @Override
    public String toString() {
        return String.format("PriceFixedPurchase{fixedPrice=%s}", fixedPrice);
    }
}

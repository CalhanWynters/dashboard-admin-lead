package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.money;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

public record PriceIntScaledPurchase(Money basePrice, Money scalingFactorPerUnit) implements PurchasePricing {

    public PriceIntScaledPurchase {
        Objects.requireNonNull(basePrice, "Base price cannot be null.");
        Objects.requireNonNull(scalingFactorPerUnit, "Scaling factor cannot be null.");

        // Ensure both money objects use the same currency
        if (!basePrice.currency().equals(scalingFactorPerUnit.currency())) {
            throw new IllegalArgumentException("Base price and scaling factor must have the same currency.");
        }

        // Ensure price components are non-negative as per original constraints
        // Note: The isZero() check is redundant if amount >= 0 covers the case
        if (basePrice.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Base price must be non-negative.");
        }
        if (scalingFactorPerUnit.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Scaling factor must be non-negative.");
        }
    }

    // Static factory for convenience
    public static PriceIntScaledPurchase of(BigDecimal base, BigDecimal scale, Currency currency) {
        // Use default precision/rounding from the Money constructor you provided earlier
        return new PriceIntScaledPurchase(new Money(base, currency), new Money(scale, currency));
    }

    // Method to calculate the total price based on quantity
    public Money calculate(BigDecimal quantity) {
        Objects.requireNonNull(quantity, "Quantity cannot be null");

        // The provided Money.multiply(int) expects an 'int' multiplier and validates it is non-negative.
        // It throws an exception if 'quantity' is not an exact integer. This is the intended behavior based on the provided code.
        int quantityInt = quantity.intValueExact();

        Money scaledAmount = scalingFactorPerUnit.multiply(quantityInt); // Uses your existing Money.multiply(int)

        return this.basePrice.add(scaledAmount); // Uses your existing Money.add(Money)
    }

    // Arithmetic method to adjust base price
    public PriceIntScaledPurchase addBasePrice(Money amountToAdd) {
        // The Money class handles currency validation internally in its add method
        Money newBasePrice = this.basePrice.add(amountToAdd);
        return new PriceIntScaledPurchase(newBasePrice, this.scalingFactorPerUnit);
    }

    // Overriding the toString method for better representation
    @Override
    public String toString() {
        return String.format("PriceScaledPurchase{basePrice=%s, scalingFactorPerUnit=%s}", basePrice, scalingFactorPerUnit);
    }
}

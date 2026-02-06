package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

/**
 * Implements a scaled pricing model (Base + Rate * Quantity) that supports fractional quantities
 * (e.g., hours of service, weight in KG, API usage).
 */
public record PriceFractScaledPurchase(Money basePrice, Money ratePerUnit) implements PurchasePricing {

    public PriceFractScaledPurchase {
        Objects.requireNonNull(basePrice, "Base price cannot be null.");
        Objects.requireNonNull(ratePerUnit, "Rate per unit cannot be null.");

        // Ensure both money objects use the same currency
        if (!basePrice.currency().equals(ratePerUnit.currency())) {
            throw new IllegalArgumentException("Base price and rate must have the same currency.");
        }

        // Ensure price components are non-negative as per business constraints
        if (basePrice.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Base price must be non-negative.");
        }
        if (ratePerUnit.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Rate per unit must be non-negative.");
        }
    }

    /**
     * Static factory for convenience.
     */
    public static PriceFractScaledPurchase of(BigDecimal base, BigDecimal rate, Currency currency) {
        // Uses default precision/rounding from the Money constructor you provided earlier
        return new PriceFractScaledPurchase(new Money(base, currency), new Money(rate, currency));
    }

    /**
     * Method to calculate the total price based on a potentially fractional quantity.
     */
    @Override
    public Money calculate(BigDecimal quantity) {
        Objects.requireNonNull(quantity, "Quantity cannot be null");
        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative.");
        }

        // Calculation: Base + (Rate * Qty)
        Money scaledAmount = ratePerUnit.multiply(quantity);
        return this.basePrice.add(scaledAmount);
    }

    @Override
    public PurchasePricing adjustedBy(double factor) {
        // Line 1: Convert double factor to BigDecimal for precision
        BigDecimal bigFactor = BigDecimal.valueOf(factor);

        // Line 2: Create a new instance with BOTH components adjusted
        return new PriceFractScaledPurchase(
                this.basePrice.multiply(bigFactor),
                this.ratePerUnit.multiply(bigFactor)
        );
    }

    // You would also need to update your sealed interface to permit this class:
    // public sealed interface PurchasePricing permits ..., MeteredScaledPurchase, ... {}
}

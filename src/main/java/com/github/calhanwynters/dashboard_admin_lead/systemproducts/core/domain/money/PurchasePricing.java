package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.money;

import java.math.BigDecimal;


/**
 * Core interface for single-purchase pricing strategies.
 * Prefixes 'Int' and 'Fract' distinguish between discrete (whole unit)
 * and fractional (metered/weighted) quantities.
 *
 * <p>Usage in the Application/Service layer:
 * <pre>{@code
 * public Money calculateTotalPrice(PurchasePricing model, BigDecimal qty) {
 *     // Polymorphism handles the specific math for Fixed, Int, or Fract models.
 *     return model.calculate(qty);
 * }
 * }</pre>
 */

public sealed interface PurchasePricing permits PriceFractTieredGradPurchase, PriceFractScaledPurchase, PriceFixedPurchase, PriceNonePurchase, PriceIntScaledPurchase, PriceIntTieredGradPurchase, PriceIntTieredVolPurchase, PriceFractTieredVolPurchase {
    /**
     * Calculates the total price for a given quantity.
     * @param quantity the number of units
     * @return the calculated Money result
     */
    Money calculate(BigDecimal quantity);
}


/*
MISSING Pricing Models that could not be modeled in this contract:

1. Subscription Logic: The "Subscription" Aggregate
Do not put "Monthly" or "Yearly" in the PurchasePricing. Instead, create a SubscriptionPlan Aggregate that contains a PurchasePricing.

    Layer: Domain Layer (Aggregate Root).
    Reason: Subscriptions have lifecycle state (Active, Trialing, Cancelled) and time boundaries, which Value Objects should not manage.

2. Bundling Logic: The "Bundle" Aggregate or Domain Service
Do not make a PriceBundleVO. Bundling is a coordination problem between multiple products.

    Layer: Domain Service.
    Reason: A bundle needs to validate the existence of multiple products and potentially override their individual prices.
    Implementation: Create a BundlePricingService that takes a List<Product> and returns a Money. This service "orchestrates" the individual PricingModelVOs of each product.

3. Dynamic Pricing: The "Pricing Engine" (Application Service)
Do not put API calls (like fetching a market rate) inside a PurchasePricing. Value Objects must be side-effect free.

    Layer: Application Service + Infrastructure.
    Reason: Dynamic pricing requires external data (weather, stock levels, competitor prices).
    Implementation:
        Application Service calls an Infrastructure Gateway (e.g., MarketDataClient).
        The gateway returns a value.
        The service then constructs a transient PriceFixedPurchase with the "new" dynamic price to pass into the rest of your system.
 */
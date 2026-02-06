package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel;

public sealed interface SimplePurchasePricing extends PurchasePricing
        permits PriceFixedPurchase, PriceNonePurchase {

    // The "Null Object" constant
    SimplePurchasePricing NONE = null;
}

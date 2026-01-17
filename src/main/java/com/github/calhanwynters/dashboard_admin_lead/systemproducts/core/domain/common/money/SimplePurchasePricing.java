package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.money;

public sealed interface SimplePurchasePricing extends PurchasePricing
        permits PriceFixedPurchase, PriceNonePurchase {
}

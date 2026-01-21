package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.money;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.Weight;

public sealed interface SimplePurchasePricing extends PurchasePricing
        permits PriceFixedPurchase, PriceNonePurchase {

    // The "Null Object" constant
    public static final SimplePurchasePricing NONE = null;
}

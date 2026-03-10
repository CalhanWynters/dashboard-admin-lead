package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel;

public enum PricingStrategyType {
    // Simple models
    FIXED,
    NONE,

    // Fractional (Metered/Weighted) models
    FRACT_TIERED_GRAD,
    FRACT_SCALED,
    FRACT_TIERED_VOL,

    // Discrete (Whole Unit) models
    INT_SCALED,
    INT_TIERED_GRAD,
    INT_TIERED_VOL
}

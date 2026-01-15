package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.money;

public sealed interface PricingModelVO permits PriceFixedVO, PriceNoneVO, PriceScaledVO, PriceTieredVO {
}

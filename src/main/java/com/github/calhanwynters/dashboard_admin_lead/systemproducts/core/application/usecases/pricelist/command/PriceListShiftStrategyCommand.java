package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PricingStrategyType;

/**
 * Command representing the intent to change the Pricing Strategy Boundary.
 * Handled by PriceListShiftStrategyHandler for SOC 2 compliant boundary re-mapping.
 */
public record PriceListShiftStrategyCommand(
        PriceListUuId priceListUuId,
        PricingStrategyType newStrategy,
        Actor actor
) {}

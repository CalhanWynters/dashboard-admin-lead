package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PricingStrategyType;

/**
 * Command representing the intent to initialize a new PriceList.
 * Handled by PriceListCreateHandler to establish the initial pricing boundary.
 */
public record PriceListCreateCommand(
        PriceListUuId priceListUuId,
        PriceListBusinessUuId businessUuId,
        PricingStrategyType strategy,
        Actor actor
) {}

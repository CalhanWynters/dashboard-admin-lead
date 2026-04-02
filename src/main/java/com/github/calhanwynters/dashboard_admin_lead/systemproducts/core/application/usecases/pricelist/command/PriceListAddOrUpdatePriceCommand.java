package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PurchasePricing;

import java.util.Currency;

/**
 * Command representing a specific Matrix update (Target + Currency + Model).
 * Handled by PriceListAddOrUpdatePriceHandler.
 */
public record PriceListAddOrUpdatePriceCommand(
        PriceListUuId priceListUuId,
        UuId targetId,           // The Product or Variant being priced
        Currency currency,
        PurchasePricing pricing, // The polymorphic pricing model
        Actor actor
) {}

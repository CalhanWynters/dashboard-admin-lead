package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

import java.util.Currency;

/**
 * Command representing the intent to remove a specific price entry from a PriceList.
 * Handled by PriceListRemovePriceHandler.
 */
public record PriceListRemovePriceCommand(
        PriceListUuId priceListUuId,
        UuId targetId,           // The Product or Variant UUID
        Currency currency,
        Actor actor
) {}

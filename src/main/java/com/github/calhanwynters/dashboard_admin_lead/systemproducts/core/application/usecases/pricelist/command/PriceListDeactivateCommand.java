package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Command representing the intent to deactivate a PriceList for operational use.
 * Handled by PriceListDeactivateHandler to trigger SOC 2 compliant state changes.
 */
public record PriceListDeactivateCommand(
        PriceListUuId priceListUuId,
        Actor actor
) {}

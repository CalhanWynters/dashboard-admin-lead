package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Command representing the intent to archive a PriceList.
 * Handled by PriceListArchiveHandler to trigger SOC 2 lifecycle transitions.
 */
public record PriceListArchiveCommand(
        PriceListUuId priceListUuId,
        Actor actor
) {}

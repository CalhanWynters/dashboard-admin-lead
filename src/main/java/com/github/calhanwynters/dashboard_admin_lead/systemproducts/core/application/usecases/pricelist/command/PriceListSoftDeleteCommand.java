package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Command representing the intent to soft-delete a PriceList.
 * Handled by PriceListSoftDeleteHandler to trigger SOC 2 compliant lifecycle transitions.
 */
public record PriceListSoftDeleteCommand(
        PriceListUuId priceListUuId,
        Actor actor
) {}

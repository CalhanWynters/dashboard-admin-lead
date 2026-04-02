package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Command representing the intent to restore a soft-deleted PriceList.
 * Handled by PriceListSoftRestoreHandler for high-privilege SOC 2 recovery.
 */
public record PriceListSoftRestoreCommand(
        PriceListUuId priceListUuId,
        Actor actor
) {}

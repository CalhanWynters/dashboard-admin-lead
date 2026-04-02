package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Command representing the intent to unarchive a PriceList.
 * Handled by PriceListUnArchiveHandler to restore the aggregate to an active state.
 */
public record PriceListUnArchiveCommand(
        PriceListUuId priceListUuId,
        Actor actor
) {}

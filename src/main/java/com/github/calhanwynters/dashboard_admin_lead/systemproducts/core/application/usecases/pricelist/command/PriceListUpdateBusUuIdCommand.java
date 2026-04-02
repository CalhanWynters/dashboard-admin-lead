package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Command representing the intent to update a PriceList's Business UUID.
 * Handled with elevated security checks (Admin role required).
 */
public record PriceListUpdateBusUuIdCommand(
        PriceListUuId priceListUuId,
        PriceListBusinessUuId newBusinessUuid,
        Actor actor
) {}

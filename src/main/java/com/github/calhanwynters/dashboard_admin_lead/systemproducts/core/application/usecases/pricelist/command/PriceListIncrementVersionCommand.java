package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Command representing the intent to manually increment a PriceList's version.
 * Handled by PriceListIncrementVersionHandler for SOC 2 compliant state tracking.
 */
public record PriceListIncrementVersionCommand(
        PriceListUuId priceListUuId,
        Actor actor
) {}

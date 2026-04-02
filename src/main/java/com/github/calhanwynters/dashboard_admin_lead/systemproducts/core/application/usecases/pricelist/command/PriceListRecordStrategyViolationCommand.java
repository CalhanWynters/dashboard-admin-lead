package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Command representing a failed or unauthorized attempt to bypass pricing strategies.
 * Handled by PriceListRecordStrategyViolationHandler for SOC 2 security auditing.
 */
public record PriceListRecordStrategyViolationCommand(
        PriceListUuId priceListUuId,
        String attemptedStrategy, // e.g., "MARKDOWN_BELOW_COST"
        Actor actor
) {}

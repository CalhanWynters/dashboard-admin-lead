package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Command representing a percentage-based shift across an entire Pricing Matrix.
 * Handled by PriceListApplyBulkAdjustmentHandler for SOC 2 compliant mass updates.
 */
public record PriceListApplyBulkAdjustmentCommand(
        PriceListUuId priceListUuId,
        String reason,         // e.g., "Annual Inflation Adjustment"
        double percentage,     // e.g., 5.0 for a 5% increase
        Actor actor
) {}

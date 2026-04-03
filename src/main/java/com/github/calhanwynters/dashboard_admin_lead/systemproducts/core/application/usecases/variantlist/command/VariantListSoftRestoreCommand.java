package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;

/**
 * Command representing the intent to restore a soft-deleted VariantList.
 * Handled by VariantListSoftRestoreHandler for SOC 2 lifecycle accountability.
 */
public record VariantListSoftRestoreCommand(
        VariantListUuId variantListUuId,
        Actor actor
) {}

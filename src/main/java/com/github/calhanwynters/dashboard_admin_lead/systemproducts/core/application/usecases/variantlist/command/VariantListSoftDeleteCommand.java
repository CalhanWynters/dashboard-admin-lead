package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;

/**
 * Command representing the intent to soft-delete a VariantList.
 * Handled by VariantListSoftDeleteHandler to manage lifecycle transitions.
 */
public record VariantListSoftDeleteCommand(
        VariantListUuId variantListUuId,
        Actor actor
) {}

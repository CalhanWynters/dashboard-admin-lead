package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;

/**
 * Command representing the intent to permanently remove a VariantList.
 * Handled with elevated security checks (Admin role required).
 */
public record VariantListHardDeleteCommand(
        VariantListUuId variantListUuId,
        Actor actor
) {}

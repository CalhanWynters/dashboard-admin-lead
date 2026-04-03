package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;

/**
 * Command for archiving a VariantList.
 * Encapsulates the target identifier and Actor for lifecycle authorization.
 */
public record VariantListArchiveCommand(
        VariantListUuId variantListUuId,
        Actor actor
) {}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;

/**
 * Command representing the intent to unarchive a VariantList.
 * Handled by VariantListUnArchiveHandler to restore active management of the collection.
 */
public record VariantListUnArchiveCommand(
        VariantListUuId variantListUuId,
        Actor actor
) {}

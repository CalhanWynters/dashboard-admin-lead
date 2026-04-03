package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;

/**
 * Command representing the intent to purge all members from a VariantList.
 * Handled by VariantListClearAllHandler to trigger bulk detachment logic.
 */
public record VariantListClearAllCommand(
        VariantListUuId variantListUuId,
        Actor actor
) {}

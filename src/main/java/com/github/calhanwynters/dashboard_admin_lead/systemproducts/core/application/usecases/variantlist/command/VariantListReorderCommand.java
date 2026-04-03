package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;

import java.util.List;

/**
 * Command representing the intent to redefine the sequence of Variants within a List.
 * Handled by VariantListReorderHandler to enforce positional invariants.
 */
public record VariantListReorderCommand(
        VariantListUuId variantListUuId,
        List<VariantsUuId> orderedVariantUuIds,
        Actor actor
) {}

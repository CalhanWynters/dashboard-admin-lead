package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;

/**
 * Command representing the intent to remove a Variant from a VariantList.
 * Handled by VariantListDetachVariantHandler to manage collection membership.
 */
public record VariantListDetachVariantCommand(
        VariantListUuId variantListUuId,
        VariantsUuId variantsUuId,
        Actor actor
) {}

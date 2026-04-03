package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;

/**
 * Command representing the intent to attach a Variant to a VariantList.
 * Encapsulates the target list, the variant to attach, and the Actor for authorization.
 */
public record VariantListAttachVariantCommand(
        VariantListUuId variantListUuId,
        VariantsUuId variantsUuId,
        Actor actor
) {}

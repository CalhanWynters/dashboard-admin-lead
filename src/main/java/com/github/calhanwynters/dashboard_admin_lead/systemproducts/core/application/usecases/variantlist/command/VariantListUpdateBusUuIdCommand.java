package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;

/**
 * Command representing the intent to update a VariantList's Business UUID.
 * Handled by VariantListUpdateBusUuIdHandler for multi-tenant ownership changes.
 */
public record VariantListUpdateBusUuIdCommand(
        VariantListUuId variantListUuId,
        VariantListBusinessUuId newBusinessUuid,
        Actor actor
) {}

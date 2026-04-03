package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;

/**
 * Command representing the intent to update a TypeList's Business UUID.
 * Handled by TypeListUpdateBusUuIdHandler for multi-tenant ownership changes.
 */
public record TypeListUpdateBusUuIdCommand(
        TypeListUuId typeListUuId,
        TypeListBusinessUuId newBusinessUuid,
        Actor actor
) {}

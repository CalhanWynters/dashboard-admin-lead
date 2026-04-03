package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;

/**
 * Command representing the intent to initialize a new TypeList.
 * Handled by TypeListCreateHandler to establish the initial collection boundary.
 */
public record TypeListCreateCommand(
        TypeListUuId typeListUuId,
        TypeListBusinessUuId businessUuId,
        Actor actor
) {}

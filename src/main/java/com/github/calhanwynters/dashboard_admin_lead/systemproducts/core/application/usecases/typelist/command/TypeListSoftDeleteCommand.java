package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;

/**
 * Command representing the intent to soft-delete a TypeList.
 * Handled by TypeListSoftDeleteHandler to manage lifecycle transitions.
 */
public record TypeListSoftDeleteCommand(
        TypeListUuId typeListUuId,
        Actor actor
) {}

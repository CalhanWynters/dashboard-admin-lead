package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;

/**
 * Command representing the intent to purge all members from a TypeList.
 * Handled by TypeListClearAllTypesHandler.
 */
public record TypeListClearAllTypesCommand(
        TypeListUuId typeListUuId,
        Actor actor
) {}

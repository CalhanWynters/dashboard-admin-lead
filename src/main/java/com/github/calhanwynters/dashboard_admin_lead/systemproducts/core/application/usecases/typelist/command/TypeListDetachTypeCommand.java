package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;

/**
 * Command representing the intent to remove a Type from a TypeList.
 * Handled by TypeListDetachTypeHandler to manage collection membership.
 */
public record TypeListDetachTypeCommand(
        TypeListUuId typeListUuId,
        TypesUuId typesUuId,
        Actor actor
) {}

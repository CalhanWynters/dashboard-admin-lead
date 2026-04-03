package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;

/**
 * Command representing the intent to attach a Type to a TypeList.
 * Handled by TypeListAttachTypeHandler to enforce collection constraints.
 */
public record TypeListAttachTypeCommand(
        TypeListUuId typeListUuId,
        TypesUuId typesUuId,
        Actor actor
) {}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;

/**
 * Command representing the intent to unarchive a TypeList.
 * Handled by TypeListUnArchiveHandler to restore active management of the collection.
 */
public record TypeListUnArchiveCommand(
        TypeListUuId typeListUuId,
        Actor actor
) {}

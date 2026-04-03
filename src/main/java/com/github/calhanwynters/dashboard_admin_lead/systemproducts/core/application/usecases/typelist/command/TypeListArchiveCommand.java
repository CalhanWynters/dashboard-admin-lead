package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;

/**
 * Command representing the intent to archive a TypeList.
 * Handled by TypeListArchiveHandler to trigger SOC 2 lifecycle transitions.
 */
public record TypeListArchiveCommand(
        TypeListUuId typeListUuId,
        Actor actor
) {}

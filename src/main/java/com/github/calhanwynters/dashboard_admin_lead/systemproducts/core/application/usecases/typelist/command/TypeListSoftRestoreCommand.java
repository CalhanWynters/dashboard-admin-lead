package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;

/**
 * Command representing the intent to restore a soft-deleted TypeList.
 * Handled by TypeListSoftRestoreHandler for SOC 2 lifecycle accountability.
 */
public record TypeListSoftRestoreCommand(
        TypeListUuId typeListUuId,
        Actor actor
) {}

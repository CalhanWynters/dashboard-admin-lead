package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;

/**
 * Command representing the intent to permanently remove a TypeList.
 * Handled with elevated security checks (Admin role required).
 */
public record TypeListHardDeleteCommand(
        TypeListUuId typeListUuId,
        Actor actor
) {}

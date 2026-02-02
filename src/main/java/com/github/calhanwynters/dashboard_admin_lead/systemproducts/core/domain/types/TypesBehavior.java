package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.TypesName;

public class TypesBehavior {

    private final TypesAggregate type;

    public TypesBehavior(TypesAggregate type) {
        DomainGuard.notNull(type, "Types Aggregate instance");
        this.type = type;
    }

    /**
     * Renames the type and refreshes the audit trail.
     */
    public TypesAggregate rename(TypesName newName, Actor actor) {
        DomainGuard.notNull(newName, "New Type Name");

        type.updateNameInternal(newName);
        type.triggerAuditUpdate(actor);

        return type;
    }
}

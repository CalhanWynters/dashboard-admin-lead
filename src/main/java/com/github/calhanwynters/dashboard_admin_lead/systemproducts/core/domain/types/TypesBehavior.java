package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.TypesName;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.TypesPhysicalSpecs;

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

    /**
     * Updates physical dimensions/weight and refreshes the audit trail.
     */
    public TypesAggregate updatePhysicalSpecs(TypesPhysicalSpecs newSpecs, Actor actor) {
        DomainGuard.notNull(newSpecs, "New Physical Specs");

        // Note: You will need to add updatePhysicalSpecsInternal(newSpecs)
        // to your TypesAggregate class
        type.updatePhysicalSpecsInternal(newSpecs);
        type.triggerAuditUpdate(actor);

        return type;
    }
}

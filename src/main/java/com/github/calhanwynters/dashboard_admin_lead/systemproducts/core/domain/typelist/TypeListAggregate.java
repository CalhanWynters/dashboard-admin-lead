package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.TypesUuId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TypeListAggregate extends BaseAggregateRoot<TypeListAggregate> {

    private final TypeListId typeListId;
    private final TypeListUuId typeListUuId;
    private final TypeListBusinessUuId typeListBusinessUuId;
    private final Set<TypesUuId> typeUuIds;

    public TypeListAggregate(TypeListId typeListId,
                             TypeListUuId typeListUuId,
                             TypeListBusinessUuId typeListBusinessUuId,
                             Set<TypesUuId> typeUuIds,
                             AuditMetadata auditMetadata) {
        super(auditMetadata);

        DomainGuard.notNull(typeListId, "TypeList ID");
        DomainGuard.notNull(typeListUuId, "TypeList UUID");
        DomainGuard.notNull(typeListBusinessUuId, "Business UUID");
        DomainGuard.notNull(typeUuIds, "Type UUID Set");

        this.typeListId = typeListId;
        this.typeListUuId = typeListUuId;
        this.typeListBusinessUuId = typeListBusinessUuId;
        this.typeUuIds = new HashSet<>(typeUuIds);
    }

    // Bridge method for Behavior access
    void triggerAuditUpdate(Actor actor) {
        this.recordUpdate(actor);
    }

    void addTypeInternal(TypesUuId typeUuId) {
        this.typeUuIds.add(typeUuId);
    }

    // Getters
    public TypeListId getTypeListId() { return typeListId; }
    public TypeListUuId getTypeListUuId() { return typeListUuId; }
    public TypeListBusinessUuId getTypeListBusinessUuId() { return typeListBusinessUuId; }
    public Set<TypesUuId> getTypeUuIds() { return Collections.unmodifiableSet(typeUuIds); }

    /**
     * Attaches a new Type to this list and refreshes the audit trail.
     */
    public void attachType(TypesUuId typeUuId, Actor actor) {
        DomainGuard.notNull(typeUuId, "Type UUID to attach");
        DomainGuard.notNull(actor, "Actor performing the update");

        // Only update and audit if it's a new addition
        if (this.typeUuIds.add(typeUuId)) {
            this.recordUpdate(actor);
        }
    }

    /**
     * Removes a Type from this list and refreshes the audit trail.
     */
    public void detachType(TypesUuId typeUuId, Actor actor) {
        DomainGuard.notNull(actor, "Actor performing the update");

        if (this.typeUuIds.remove(typeUuId)) {
            this.recordUpdate(actor);
        }
    }
}

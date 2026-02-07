package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.events.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TypeListAggregate extends BaseAggregateRoot<TypeListAggregate> {

    private final TypeListId typeListId;
    private final TypeListUuId typeListUuId;
    private final TypeListBusinessUuId typeListBusinessUuId;
    private final Set<TypesUuId> typeUuIds;
    private boolean deleted = false; // Track lifecycle state

    public TypeListAggregate(TypeListId typeListId,
                             TypeListUuId typeListUuId,
                             TypeListBusinessUuId typeListBusinessUuId,
                             Set<TypesUuId> typeUuIds,
                             boolean deleted, // Add this
                             AuditMetadata auditMetadata) {
        super(auditMetadata);
        this.typeListId = typeListId;
        this.typeListUuId = DomainGuard.notNull(typeListUuId, "TypeList UUID");
        this.typeListBusinessUuId = DomainGuard.notNull(typeListBusinessUuId, "Business UUID");
        this.typeUuIds = new HashSet<>(typeUuIds != null ? typeUuIds : Collections.emptySet());
        this.deleted = deleted;
    }

    public static TypeListAggregate create(TypeListUuId uuId, TypeListBusinessUuId bUuId, Actor actor) {
        // Line 1: Auth
        TypeListBehavior.verifyCreationAuthority(actor);

        TypeListAggregate aggregate = new TypeListAggregate(
                null, uuId, bUuId, new HashSet<>(), false, AuditMetadata.create(actor)
        );
        aggregate.registerEvent(new TypeListCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    public void attachType(TypesUuId typeUuId, Actor actor) {
        // Line 1: Logic & Auth
        TypeListBehavior.ensureActive(this.deleted);
        TypeListBehavior.ensureCanAttach(this.typeUuIds, typeUuId, actor);

        // Line 2: Execution
        this.applyChange(actor,
                new TypeAttachedEvent(this.typeListUuId, typeUuId, actor),
                () -> this.typeUuIds.add(typeUuId)
        );
    }

    public void detachType(TypesUuId typeUuId, Actor actor) {
        // Line 1: Logic & Auth
        TypeListBehavior.ensureActive(this.deleted);
        TypeListBehavior.ensureCanDetach(this.typeUuIds, typeUuId, actor);

        this.applyChange(actor,
                new TypeDetachedEvent(this.typeListUuId, typeUuId, actor),
                () -> this.typeUuIds.remove(typeUuId)
        );
    }

    public void softDelete(Actor actor) {
        // Line 1: Auth
        TypeListBehavior.ensureActive(this.deleted);
        TypeListBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new TypeListSoftDeletedEvent(this.typeListUuId, actor),
                () -> this.deleted = true
        );
    }

    public void hardDelete(Actor actor) {
        // Line 1: Admin-only Auth
        TypeListBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new TypeListHardDeletedEvent(this.typeListUuId, actor),
                null
        );
    }

    public void clearAllTypes(Actor actor) {
        // Line 1: Auth
        TypeListBehavior.ensureActive(this.deleted);
        TypeListBehavior.verifyMembershipAuthority(actor);

        if (this.typeUuIds.isEmpty()) return;

        this.applyChange(actor,
                new TypeListClearedEvent(this.typeListUuId, actor),
                this.typeUuIds::clear
        );
    }

    public void restore(Actor actor) {
        // Line 1: Admin Auth
        if (!this.deleted) return;
        TypeListBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new TypeListRestoredEvent(this.typeListUuId, actor),
                () -> this.deleted = false
        );
    }

    // --- ACCESSORS ---
    public boolean isDeleted() { return deleted; }
    public TypeListId getTypeListId() { return typeListId; }
    public TypeListUuId getTypeListUuId() { return typeListUuId; }
    public TypeListBusinessUuId getTypeListBusinessUuId() { return typeListBusinessUuId; }
    public Set<TypesUuId> getTypeUuIds() { return Collections.unmodifiableSet(typeUuIds); }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.events.*;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;

/**
 * Refactored TypeList Aggregate (2026 Edition).
 * Manages collections of Types with SOC 2 compliant lifecycle and membership logic.
 */
public class TypeListAggregate extends BaseAggregateRoot<
        TypeListAggregate,
        TypeListId,
        TypeListUuId,
        TypeListBusinessUuId
        > {

    private final Set<TypesUuId> typeUuIds;

    public TypeListAggregate(TypeListId id, TypeListUuId uuId, TypeListBusinessUuId businessUuId,
                             Set<TypesUuId> typeUuIds, AuditMetadata auditMetadata,
                             LifecycleState lifecycleState, Long optLockVer,
                             Integer schemaVer, OffsetDateTime lastSyncedAt) {
        super(id, uuId, businessUuId, auditMetadata, optLockVer, schemaVer, lastSyncedAt);
        this.typeUuIds = new HashSet<>(typeUuIds != null ? typeUuIds : Collections.emptySet());
        this.lifecycleState = lifecycleState;
    }

    // --- FACTORY ---

    public static TypeListAggregate create(TypeListUuId uuId, TypeListBusinessUuId bUuId, Actor actor) {
        TypeListBehavior.validateCreation(uuId, bUuId, actor);

        TypeListAggregate aggregate = new TypeListAggregate(
                null, uuId, bUuId, new HashSet<>(),
                AuditMetadata.create(actor), new LifecycleState(false, false),
                0L, 1, null
        );

        aggregate.registerEvent(new TypeListCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void updateBusinessUuId(TypeListBusinessUuId newId, Actor actor) {
        this.executeBusinessUuIdUpdate(newId, actor,
                val -> new TypeListBusinessUuIdChangedEvent(this.uuId, this.businessUuId, val, actor)
        );
    }

    public void attachType(TypesUuId typeUuId, Actor actor) {
        this.applyDomainChange(actor, typeUuId,
                (next, auth) -> {
                    TypeListBehavior.ensureCanAttach(this.typeUuIds, next, auth);
                    return next;
                },
                val -> new TypeAttachedEvent(this.uuId, val, actor),
                this.typeUuIds::add
        );
    }

    public void detachType(TypesUuId typeUuId, Actor actor) {
        this.applyDomainChange(actor, typeUuId,
                (next, auth) -> {
                    TypeListBehavior.ensureCanDetach(this.typeUuIds, next, auth);
                    return next;
                },
                val -> new TypeDetachedEvent(this.uuId, val, actor),
                this.typeUuIds::remove
        );
    }

    public void clearAllTypes(Actor actor) {
        ensureActive(); // From BaseAggregateRoot
        TypeListBehavior.verifyMembershipAuthority(actor);

        if (this.typeUuIds.isEmpty()) return;

        this.applyChange(actor,
                new TypeListClearedEvent(this.uuId, actor),
                this.typeUuIds::clear
        );
    }

    public void syncToKafka(Actor actor) {
        this.executeSync(actor,
                auth -> new TypeListDataSyncedEvent(this.uuId, this.businessUuId,
                        this.typeUuIds, this.lifecycleState, auth)
        );
    }

    // --- LIFECYCLE (Standardized via Base Engine) ---

    public void archive(Actor actor) {
        this.executeArchive(actor, new TypeListArchivedEvent(this.uuId, actor));
    }

    public void unarchive(Actor actor) {
        this.executeUnarchive(actor, new TypeListUnarchivedEvent(this.uuId, actor));
    }

    public void softDelete(Actor actor) {
        this.executeSoftDelete(actor, new TypeListSoftDeletedEvent(this.uuId, actor));
    }

    public void restore(Actor actor) {
        this.executeRestore(actor, new TypeListRestoredEvent(this.uuId, actor));
    }

    public void hardDelete(Actor actor) {
        this.executeHardDelete(actor, new TypeListHardDeletedEvent(this.uuId, actor));
    }

    // --- GETTERS ---
    public Set<TypesUuId> getTypeUuIds() { return Collections.unmodifiableSet(typeUuIds); }
}

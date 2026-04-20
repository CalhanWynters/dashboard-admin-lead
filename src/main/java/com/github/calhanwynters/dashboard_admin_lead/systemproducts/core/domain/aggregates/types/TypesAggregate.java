package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.events.*;

import java.time.OffsetDateTime;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

/**
 * Refactored Types Aggregate (2026 Edition).
 * Manages physical product classifications with spec-change detection.
 */
public class TypesAggregate extends BaseAggregateRoot<
        TypesAggregate,
        TypesId,
        TypesUuId,
        TypesBusinessUuId
        > {

    private TypesName typesName;
    private TypesRegion typesRegion;
    private TypesPhysicalSpecs typesPhysicalSpecs;

    public TypesAggregate(TypesId id, TypesUuId uuId, TypesBusinessUuId businessUuId,
                          TypesName name, TypesRegion region, TypesPhysicalSpecs specs,
                          AuditMetadata auditMetadata, LifecycleState lifecycleState,
                          Long optLockVer, Integer schemaVer, OffsetDateTime lastSyncedAt) {
        super(id, uuId, businessUuId, auditMetadata, optLockVer, schemaVer, lastSyncedAt);
        this.typesName = name;
        this.typesPhysicalSpecs = specs;
        this.lifecycleState = lifecycleState;
    }

    // --- FACTORY ---

    public static TypesAggregate create(TypesUuId uuId, TypesBusinessUuId bUuId,
                                        TypesName name, TypesRegion region,
                                        TypesPhysicalSpecs specs, Actor actor) {
        TypesBehavior.validateCreation(uuId, bUuId, actor);

        TypesAggregate aggregate = new TypesAggregate(
                null, uuId, bUuId, name, region, specs,
                AuditMetadata.create(actor), new LifecycleState(false, false),
                0L, 1, null
        );

        aggregate.registerEvent(new TypeCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void updateBusinessUuId(TypesBusinessUuId newId, Actor actor) {
        this.executeBusinessUuIdUpdate(newId, actor,
                val -> new TypeBusinessUuIdChangedEvent(this.uuId, this.businessUuId, val, actor)
        );
    }

    public void rename(TypesName newName, Actor actor) {
        this.applyDomainChange(actor, newName,
                (next, auth) -> TypesBehavior.evaluateRename(this.typesName, next, auth),
                val -> new TypeRenamedEvent(this.uuId, val, actor),
                val -> this.typesName = val
        );
    }

    public void updateRegion(TypesRegion newRegion, Actor actor) {
        this.applyDomainChange(actor, TypesRegion.from(newRegion.value()), // Use .from() based on your record definition
                (next, auth) -> TypesBehavior.evaluateRegionTransition(this.typesRegion, next, auth),
                val -> new TypesRegionUpdatedEvent(this.uuId, this.typesRegion, val, actor),
                val -> this.typesRegion = val
        );
    }

    public void updatePhysicalSpecs(TypesPhysicalSpecs newSpecs, Actor actor) {
        this.applyDomainChange(actor, newSpecs,
                (next, auth) -> {
                    TypesBehavior.validateSpecs(next, auth);
                    return next;
                },
                val -> new TypePhysicalSpecsUpdatedEvent(this.uuId, val, actor),
                val -> {
                    // Check for specialized spec shifts to register secondary events
                    boolean dimensionsChanged = TypesBehavior.detectDimensionChange(this.typesPhysicalSpecs, val);
                    boolean weightShifted = TypesBehavior.detectWeightShift(this.typesPhysicalSpecs, val);

                    this.typesPhysicalSpecs = val;

                    if (dimensionsChanged) this.registerEvent(new TypeDimensionsChangedEvent(this.uuId, actor));
                    if (weightShifted) this.registerEvent(new TypeWeightClassShiftedEvent(this.uuId, actor));
                }
        );
    }

    public void recordSpecsConflict(String details, Actor actor) {
        // SOC 2: Verify restricted authority for recording detected system conflicts
        if (!actor.hasRole(Actor.ROLE_ADMIN) && !Actor.SYSTEM.equals(actor)) {
            throw new DomainAuthorizationException("Unauthorized conflict recording.", "SEC-403", actor);
        }

        // Pure audit event, no state mutation
        this.applyChange(actor, new TypeSpecsConflictDetectedEvent(this.uuId, details, actor), null);
    }

    public void syncToKafka(Actor actor) {
        this.executeSync(actor,
                auth -> new TypeDataSyncedEvent(this.uuId, this.businessUuId, this.typesName,
                        this.typesPhysicalSpecs, this.lifecycleState, auth)
        );
    }

    // --- LIFECYCLE (Standardized via Base Engine) ---

    public void archive(Actor actor) {
        this.executeArchive(actor, new TypeArchivedEvent(this.uuId, actor));
    }

    public void unarchive(Actor actor) {
        this.executeUnarchive(actor, new TypeUnarchivedEvent(this.uuId, actor));
    }

    public void softDelete(Actor actor) {
        this.executeSoftDelete(actor, new TypeSoftDeletedEvent(this.uuId, actor));
    }

    public void restore(Actor actor) {
        this.executeRestore(actor, new TypeRestoredEvent(this.uuId, actor));
    }

    public void hardDelete(Actor actor) {
        this.executeHardDelete(actor, new TypeHardDeletedEvent(this.uuId, actor));
    }

    // --- GETTERS ---
    public TypesName getTypesName() { return typesName; }
    public TypesRegion getTypesRegion() {return typesRegion; }
    public TypesPhysicalSpecs getTypesPhysicalSpecs() { return typesPhysicalSpecs; }
}
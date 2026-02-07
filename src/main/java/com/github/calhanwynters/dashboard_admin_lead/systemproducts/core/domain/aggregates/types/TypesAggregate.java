package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.events.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

/**
 * Aggregate Root for Product Types.
 * Standardized via Rich Domain Model to ensure physical spec integrity and auditing.
 */
public class TypesAggregate extends BaseAggregateRoot<TypesAggregate> {

    private final TypesId typesId;
    private final TypesUuId typesUuId;
    private final TypesBusinessUuId typesBusinessUuId;

    private TypesName typesName;
    private TypesPhysicalSpecs typesPhysicalSpecs;
    private boolean deleted;

    public TypesAggregate(TypesId typesId,
                          TypesUuId typesUuId,
                          TypesBusinessUuId typesBusinessUuId,
                          TypesName typesName,
                          TypesPhysicalSpecs typesPhysicalSpecs,
                          boolean deleted,
                          AuditMetadata auditMetadata) {
        super(auditMetadata);
        this.typesId = typesId;
        this.typesUuId = DomainGuard.notNull(typesUuId, "Types UUID");
        this.typesBusinessUuId = DomainGuard.notNull(typesBusinessUuId, "Business UUID");
        this.typesName = DomainGuard.notNull(typesName, "Types Name");
        this.typesPhysicalSpecs = DomainGuard.notNull(typesPhysicalSpecs, "Types Physical Specs");
        this.deleted = deleted;
    }

    /**
     * Static Factory for creating new Types.
     */
    public static TypesAggregate create(TypesUuId uuId, TypesBusinessUuId bUuId,
                                        TypesName name, TypesPhysicalSpecs specs, Actor actor) {
        // Line 1: Logic & Auth
        TypesBehavior.verifyCreationAuthority(actor);

        TypesAggregate aggregate = new TypesAggregate(null, uuId, bUuId, name, specs, false, AuditMetadata.create(actor));
        aggregate.registerEvent(new TypeCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void rename(TypesName newName, Actor actor) {
        // Line 1: Logic & Auth
        TypesBehavior.ensureActive(this.deleted);
        var validatedName = TypesBehavior.evaluateRename(this.typesName, newName, actor);

        // Line 2: Side-Effect Execution
        this.applyChange(actor,
                new TypeRenamedEvent(this.typesUuId, validatedName, actor),
                () -> this.typesName = validatedName
        );
    }

    public void updatePhysicalSpecs(TypesPhysicalSpecs newSpecs, Actor actor) {
        // Line 1: Logic & Auth
        TypesBehavior.ensureActive(this.deleted);
        TypesBehavior.validateSpecs(newSpecs, actor);

        boolean dimensionsChanged = TypesBehavior.detectDimensionChange(this.typesPhysicalSpecs, newSpecs);
        boolean weightShifted = TypesBehavior.detectWeightShift(this.typesPhysicalSpecs, newSpecs);

        // Line 2: Side-effect
        this.applyChange(actor,
                new TypePhysicalSpecsUpdatedEvent(this.typesUuId, newSpecs, actor),
                () -> {
                    this.typesPhysicalSpecs = newSpecs;

                    // Specialized granular events
                    if (dimensionsChanged) {
                        this.registerEvent(new TypeDimensionsChangedEvent(this.typesUuId, actor));
                    }
                    if (weightShifted) {
                        this.registerEvent(new TypeWeightClassShiftedEvent(this.typesUuId, actor));
                    }
                }
        );
    }

    public void recordSpecsConflict(String details, Actor actor) {
        // Security check: Only System or Admin can record detected conflicts
        if (!actor.hasRole(Actor.ROLE_ADMIN) && !actor.equals(Actor.SYSTEM)) {
            throw new com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException(
                    "Unauthorized conflict recording.", "SEC-403", actor);
        }

        this.applyChange(actor, new TypeSpecsConflictDetectedEvent(this.typesUuId, details, actor), null);
    }

    public void softDelete(Actor actor) {
        // Line 1: Logic & Auth
        TypesBehavior.ensureActive(this.deleted);
        TypesBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor, new TypeSoftDeletedEvent(this.typesUuId, actor), () -> this.deleted = true);
    }

    public void restore(Actor actor) {
        // Line 1: Logic & Auth
        if (!this.deleted) return;
        TypesBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor, new TypeRestoredEvent(this.typesUuId, actor), () -> this.deleted = false);
    }

    public void hardDelete(Actor actor) {
        // Line 1: Logic & Admin Auth
        TypesBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor, new TypeHardDeletedEvent(this.typesUuId, actor), null);
    }

    // --- ACCESSORS ---
    public boolean isDeleted() { return deleted; }
    public TypesId getTypesId() { return typesId; }
    public TypesUuId getTypesUuId() { return typesUuId; }
    public TypesBusinessUuId getTypesBusinessUuId() { return typesBusinessUuId; }
    public TypesName getTypesName() { return typesName; }
    public TypesPhysicalSpecs getTypesPhysicalSpecs() { return typesPhysicalSpecs; }
}

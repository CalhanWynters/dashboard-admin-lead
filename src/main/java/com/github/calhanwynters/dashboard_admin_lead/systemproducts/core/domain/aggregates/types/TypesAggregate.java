package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
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
    private ProductBooleans productBooleans; // Replaced boolean deleted

    public TypesAggregate(TypesId typesId,
                          TypesUuId typesUuId,
                          TypesBusinessUuId typesBusinessUuId,
                          TypesName typesName,
                          TypesPhysicalSpecs typesPhysicalSpecs,
                          ProductBooleans productBooleans, // Updated parameter
                          AuditMetadata auditMetadata) {
        super(auditMetadata);
        this.typesId = typesId;
        this.typesUuId = DomainGuard.notNull(typesUuId, "Types UUID");
        this.typesBusinessUuId = DomainGuard.notNull(typesBusinessUuId, "Business UUID");
        this.typesName = DomainGuard.notNull(typesName, "Types Name");
        this.typesPhysicalSpecs = DomainGuard.notNull(typesPhysicalSpecs, "Types Physical Specs");
        // Null-safe assignment for the record
        this.productBooleans = productBooleans != null ? productBooleans : new ProductBooleans(false, false);
    }

    public static TypesAggregate create(TypesUuId uuId, TypesBusinessUuId bUuId,
                                        TypesName name, TypesPhysicalSpecs specs, Actor actor) {
        TypesBehavior.verifyCreationAuthority(actor);

        // Initialize with default ProductBooleans state
        TypesAggregate aggregate = new TypesAggregate(
                null, uuId, bUuId, name, specs, new ProductBooleans(false, false), AuditMetadata.create(actor)
        );
        aggregate.registerEvent(new TypeCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void rename(TypesName newName, Actor actor) {
        TypesBehavior.ensureActive(this.productBooleans.softDeleted()); // Accessing via record
        var validatedName = TypesBehavior.evaluateRename(this.typesName, newName, actor);

        this.applyChange(actor,
                new TypeRenamedEvent(this.typesUuId, validatedName, actor),
                () -> this.typesName = validatedName
        );
    }

    public void updatePhysicalSpecs(TypesPhysicalSpecs newSpecs, Actor actor) {
        TypesBehavior.ensureActive(this.productBooleans.softDeleted());
        TypesBehavior.validateSpecs(newSpecs, actor);

        boolean dimensionsChanged = TypesBehavior.detectDimensionChange(this.typesPhysicalSpecs, newSpecs);
        boolean weightShifted = TypesBehavior.detectWeightShift(this.typesPhysicalSpecs, newSpecs);

        this.applyChange(actor,
                new TypePhysicalSpecsUpdatedEvent(this.typesUuId, newSpecs, actor),
                () -> {
                    this.typesPhysicalSpecs = newSpecs;
                    if (dimensionsChanged) this.registerEvent(new TypeDimensionsChangedEvent(this.typesUuId, actor));
                    if (weightShifted) this.registerEvent(new TypeWeightClassShiftedEvent(this.typesUuId, actor));
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
        TypesBehavior.ensureActive(this.productBooleans.softDeleted());
        TypesBehavior.verifyLifecycleAuthority(actor);

        // Replace the record to update state
        this.applyChange(actor,
                new TypeSoftDeletedEvent(this.typesUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), true)
        );
    }

    public void restore(Actor actor) {
        if (!this.productBooleans.softDeleted()) return;
        TypesBehavior.verifyLifecycleAuthority(actor);

        // Replace the record to update state
        this.applyChange(actor,
                new TypeRestoredEvent(this.typesUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), false)
        );
    }

    public void hardDelete(Actor actor) {
        TypesBehavior.verifyLifecycleAuthority(actor);
        this.applyChange(actor, new TypeHardDeletedEvent(this.typesUuId, actor), null);
    }

    public void archive(Actor actor) {
        // Line 1: Auth
        TypesBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record instance)
        this.applyChange(actor,
                new TypeArchivedEvent(this.typesUuId, actor),
                () -> this.productBooleans = new ProductBooleans(true, this.productBooleans.softDeleted())
        );
    }

    public void unarchive(Actor actor) {
        // Line 1: Auth
        TypesBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record instance)
        this.applyChange(actor,
                new TypeUnarchivedEvent(this.typesUuId, actor),
                () -> this.productBooleans = new ProductBooleans(false, this.productBooleans.softDeleted())
        );
    }


    // --- ACCESSORS ---
    public ProductBooleans getProductBooleans() { return productBooleans; }
    public boolean isDeleted() { return productBooleans.softDeleted(); }
    public TypesId getTypesId() { return typesId; }
    public TypesUuId getTypesUuId() { return typesUuId; }
    public TypesBusinessUuId getTypesBusinessUuId() { return typesBusinessUuId; }
    public TypesName getTypesName() { return typesName; }
    public TypesPhysicalSpecs getTypesPhysicalSpecs() { return typesPhysicalSpecs; }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.events.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.*;

public class TypesAggregate extends BaseAggregateRoot<TypesAggregate> {

    private final TypesId typesId;
    private final TypesUuId typesUuId;

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
        TypesBusinessUuId typesBusinessUuId1 = DomainGuard.notNull(typesBusinessUuId, "Business UUID");
        this.typesName = DomainGuard.notNull(typesName, "Types Name");
        this.typesPhysicalSpecs = DomainGuard.notNull(typesPhysicalSpecs, "Types Physical Specs");
        this.deleted = deleted;
    }

    public static TypesAggregate create(TypesUuId uuId, TypesBusinessUuId bUuId,
                                        TypesName name, TypesPhysicalSpecs specs, Actor actor) {
        TypesAggregate aggregate = new TypesAggregate(null, uuId, bUuId, name, specs, false, AuditMetadata.create(actor));
        aggregate.registerEvent(new TypeCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void rename(TypesName newName, Actor actor) {
        TypesBehavior.ensureActive(this.deleted);
        var validatedName = TypesBehavior.evaluateRename(this.typesName, newName);

        this.applyChange(actor,
                new TypeRenamedEvent(this.typesUuId, validatedName, actor),
                () -> this.typesName = validatedName
        );
    }

    public void updatePhysicalSpecs(TypesPhysicalSpecs newSpecs, Actor actor) {
        TypesBehavior.ensureActive(this.deleted);
        TypesBehavior.validateSpecs(newSpecs);

        // Line 1: Logic - Detect granular changes using the .value() accessor
        boolean dimensionsChanged = TypesBehavior.detectDimensionChange(this.typesPhysicalSpecs, newSpecs);
        boolean weightShifted = TypesBehavior.detectWeightShift(this.typesPhysicalSpecs, newSpecs);

        // Line 2: Side-effect
        this.applyChange(actor,
                new TypePhysicalSpecsUpdatedEvent(this.typesUuId, newSpecs, actor),
                () -> {
                    this.typesPhysicalSpecs = newSpecs;

                    // Fire specialized granular events
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
        // This fires the specific conflict event without changing state
        this.applyChange(actor, new TypeSpecsConflictDetectedEvent(this.typesUuId, details, actor), null);
    }



    public void archive(Actor actor) {
        TypesBehavior.ensureActive(this.deleted);

        // Pass null as the 3rd argument since archiving currently only fires an event
        this.applyChange(actor,
                new TypeArchivedEvent(this.typesUuId, actor),
                null
        );
    }


    public void restore(Actor actor) {
        if (!this.deleted) return;
        this.applyChange(actor,
                new TypeRestoredEvent(this.typesUuId, actor),
                () -> this.deleted = false
        );
    }

    public void softDelete(Actor actor) {
        TypesBehavior.ensureActive(this.deleted);
        this.applyChange(actor, new TypeSoftDeletedEvent(this.typesUuId, actor), () -> this.deleted = true);
    }

    public void hardDelete(Actor actor) {
        this.applyChange(actor, new TypeHardDeletedEvent(this.typesUuId, actor), null);
    }

    // --- ACCESSORS ---
    public boolean isDeleted() { return deleted; }
    public TypesId getTypesId() { return typesId; }
    public TypesUuId getTypesUuId() { return typesUuId; }
    public TypesName getTypesName() { return typesName; }
    public TypesPhysicalSpecs getTypesPhysicalSpecs() { return typesPhysicalSpecs; }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.*;

/**
 * Aggregate Root for Product Types.
 * Encapsulates naming and physical specifications with mandatory audit attribution.
 */
public class TypesAggregate extends BaseAggregateRoot<TypesAggregate> {

    private final TypesId typesId;
    private final TypesUuId typesUuId;
    private final TypesBusinessUuId typesBusinessUuId;

    private TypesName typesName;
    private TypesPhysicalSpecs typesPhysicalSpecs;

    public TypesAggregate(TypesId typesId,
                          TypesUuId typesUuId,
                          TypesBusinessUuId typesBusinessUuId,
                          TypesName typesName,
                          TypesPhysicalSpecs typesPhysicalSpecs,
                          AuditMetadata auditMetadata) {
        super(auditMetadata);

        DomainGuard.notNull(typesId, "Types ID");
        DomainGuard.notNull(typesUuId, "Types UUID");
        DomainGuard.notNull(typesBusinessUuId, "Business UUID");
        DomainGuard.notNull(typesName, "Types Name");
        DomainGuard.notNull(typesPhysicalSpecs, "Types Physical Specs");

        this.typesId = typesId;
        this.typesUuId = typesUuId;
        this.typesBusinessUuId = typesBusinessUuId;
        this.typesName = typesName;
        this.typesPhysicalSpecs = typesPhysicalSpecs;
    }

    // --- DOMAIN ACTIONS ---

    /**
     * Atomically renames the Type and refreshes the audit trail.
     */
    public void rename(TypesName newName, Actor actor) {
        DomainGuard.notNull(newName, "New Type Name");
        DomainGuard.notNull(actor, "Actor performing the rename");

        this.typesName = newName;
        this.recordUpdate(actor);
    }

    /**
     * Atomically updates physical dimensions/weight and refreshes the audit trail.
     */
    public void updatePhysicalSpecs(TypesPhysicalSpecs newSpecs, Actor actor) {
        DomainGuard.notNull(newSpecs, "New Physical Specs");
        DomainGuard.notNull(actor, "Actor performing the update");

        this.typesPhysicalSpecs = newSpecs;
        this.recordUpdate(actor);
    }

    // --- ACCESSORS ---
    public TypesId getTypesId() { return typesId; }
    public TypesUuId getTypesUuId() { return typesUuId; }
    public TypesBusinessUuId getTypesBusinessUuId() { return typesBusinessUuId; }
    public TypesName getTypesName() { return typesName; }
    public TypesPhysicalSpecs getTypesPhysicalSpecs() { return typesPhysicalSpecs; }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.*;

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

    /**
     * Internal state change for the name.
     */
    void updateNameInternal(TypesName newName) {
        this.typesName = newName;
    }

    /**
     * Internal state change for physical specs.
     */
    void updatePhysicalSpecsInternal(TypesPhysicalSpecs newSpecs) {
        this.typesPhysicalSpecs = newSpecs;
    }

    public void triggerAuditUpdate(Actor actor) {
        DomainGuard.notNull(actor, "Actor");
        // Assuming your BaseAggregateRoot holds an AuditMetadata field
        this.auditMetadata = this.auditMetadata.update(actor);
    }


    // Getters
    public TypesId getTypesId() { return typesId; }
    public TypesUuId getTypesUuId() { return typesUuId; }
    public TypesBusinessUuId getTypesBusinessUuId() { return typesBusinessUuId; }
    public TypesName getTypesName() { return typesName; }
    public TypesPhysicalSpecs getTypesPhysicalSpecs() { return typesPhysicalSpecs; }
}

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
    private TypesName typesName; // Removed final to allow Behavior-driven updates

    public TypesAggregate(TypesId typesId,
                          TypesUuId typesUuId,
                          TypesBusinessUuId typesBusinessUuId,
                          TypesName typesName,
                          AuditMetadata auditMetadata) {
        super(auditMetadata);

        DomainGuard.notNull(typesId, "Types ID");
        DomainGuard.notNull(typesUuId, "Types UUID");
        DomainGuard.notNull(typesBusinessUuId, "Business UUID");
        DomainGuard.notNull(typesName, "Types Name");

        this.typesId = typesId;
        this.typesUuId = typesUuId;
        this.typesBusinessUuId = typesBusinessUuId;
        this.typesName = typesName;
    }

    /**
     * Audit Bridge for TypesBehavior.
     */
    void triggerAuditUpdate(Actor actor) {
        this.recordUpdate(actor);
    }

    /**
     * Internal mutation for Behavior access.
     */
    void updateNameInternal(TypesName newName) {
        this.typesName = newName;
    }

    // Getters
    public TypesId getTypesId() { return typesId; }
    public TypesUuId getTypesUuId() { return typesUuId; }
    public TypesBusinessUuId getTypesBusinessUuId() { return typesBusinessUuId; }
    public TypesName getTypesName() { return typesName; }
}

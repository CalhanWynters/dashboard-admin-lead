package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.TypesId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.TypesUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.TypesBusinessUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.TypesName;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import org.springframework.data.domain.AbstractAggregateRoot;

public class TypesAggregate extends AbstractAggregateRoot<TypesAggregate> {

    private final TypesId typesId;
    private final TypesUuId typesUuId;
    private final TypesBusinessUuId typesBusinessUuId;
    private final TypesName typesName;

    public TypesAggregate(TypesId typesId,
                          TypesUuId typesUuId,
                          TypesBusinessUuId typesBusinessUuId,
                          TypesName typesName) {

        // Validation checks
        DomainGuard.notNull(typesId, "TypesAggregate ID");
        DomainGuard.notNull(typesUuId, "TypesAggregate UUID");
        DomainGuard.notNull(typesBusinessUuId, "TypesAggregate Business UUID");
        DomainGuard.notNull(typesName, "TypesAggregate Name");

        this.typesId = typesId;
        this.typesUuId = typesUuId;
        this.typesBusinessUuId = typesBusinessUuId;
        this.typesName = typesName;
    }

    // Getters
    public TypesId getTypesId() {
        return typesId;
    }

    public TypesUuId getTypesUuId() {
        return typesUuId;
    }

    public TypesBusinessUuId getTypesBusinessUuId() {
        return typesBusinessUuId;
    }

    public TypesName getTypesName() {
        return typesName;
    }
}

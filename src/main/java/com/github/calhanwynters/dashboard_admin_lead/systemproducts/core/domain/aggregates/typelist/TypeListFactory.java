package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;

import java.util.Set;

public class TypeListFactory {

    public static TypeListAggregate create(TypeListBusinessUuId bizId, Actor creator) {
        return TypeListAggregate.create(TypeListUuId.generate(), bizId, creator);
    }

    public static TypeListAggregate reconstitute(
            TypeListId id, TypeListUuId uuId, TypeListBusinessUuId bizId,
            Set<TypesUuId> ids, ProductBooleans productBooleans, // Replaced boolean
            AuditMetadata audit) {

        return new TypeListAggregate(id, uuId, bizId, ids, productBooleans, audit);
    }
}

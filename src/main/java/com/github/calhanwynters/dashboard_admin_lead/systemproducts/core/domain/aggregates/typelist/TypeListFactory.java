package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleansLEGACY;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;

import java.util.Set;

public class TypeListFactory {

    public static TypeListAggregateLEGACY create(TypeListBusinessUuId bizId, Actor creator) {
        return TypeListAggregateLEGACY.create(TypeListUuId.generate(), bizId, creator);
    }

    public static TypeListAggregateLEGACY reconstitute(
            TypeListId id, TypeListUuId uuId, TypeListBusinessUuId bizId,
            Set<TypesUuId> ids, ProductBooleansLEGACY productBooleansLEGACY, // Replaced boolean
            AuditMetadata audit) {

        return new TypeListAggregateLEGACY(id, uuId, bizId, ids, productBooleansLEGACY, audit);
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.TypesUuId;

import java.util.Set;

public class TypeListFactory {

    public static TypeListAggregate create(TypeListBusinessUuId bizId, Set<TypesUuId> ids, Actor creator) {
        return new TypeListAggregate(
                TypeListId.NONE, // Much cleaner than magic strings
                TypeListUuId.generate(),
                bizId,
                ids,
                AuditMetadata.create(creator)
        );
    }

    public static TypeListAggregate reconstitute(
            TypeListId id, TypeListUuId uuId, TypeListBusinessUuId bizId,
            Set<TypesUuId> ids, AuditMetadata audit) {
        return new TypeListAggregate(id, uuId, bizId, ids, audit);
    }
}

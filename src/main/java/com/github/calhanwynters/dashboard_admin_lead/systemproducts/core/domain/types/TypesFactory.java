package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.*;

public class TypesFactory {

    public static TypesAggregate create(TypesBusinessUuId bizId, TypesName name, Actor creator) {
        return new TypesAggregate(
                TypesId.of(0L),
                TypesUuId.generate(),
                bizId,
                name,
                AuditMetadata.create(creator)
        );
    }

    public static TypesAggregate reconstitute(
            TypesId id, TypesUuId uuId, TypesBusinessUuId bizId,
            TypesName name, AuditMetadata audit) {
        return new TypesAggregate(id, uuId, bizId, name, audit);
    }
}

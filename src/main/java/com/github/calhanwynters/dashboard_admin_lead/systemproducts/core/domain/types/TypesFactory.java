package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.*;

public class TypesFactory {

    /**
     * Used for creating a brand-new aggregate for the first time.
     */
    public static TypesAggregate create(
            TypesBusinessUuId bizId,
            TypesName name,
            TypesPhysicalSpecs physicalSpecs, // New parameter
            Actor creator) {
        return new TypesAggregate(
                TypesId.of(0L),
                TypesUuId.generate(),
                bizId,
                name,
                physicalSpecs,
                AuditMetadata.create(creator)
        );
    }

    /**
     * Used for recreating an aggregate from a database or storage.
     */
    public static TypesAggregate reconstitute(
            TypesId id,
            TypesUuId uuId,
            TypesBusinessUuId bizId,
            TypesName name,
            TypesPhysicalSpecs physicalSpecs, // New parameter
            AuditMetadata audit) {
        return new TypesAggregate(id, uuId, bizId, name, physicalSpecs, audit);
    }
}

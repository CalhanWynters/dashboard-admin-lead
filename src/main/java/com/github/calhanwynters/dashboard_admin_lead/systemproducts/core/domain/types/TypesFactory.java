package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.*;

public class TypesFactory {

    /**
     * Delegates to the Aggregate static factory to ensure the
     * TypeCreatedEvent is properly registered in the AbstractAggregateRoot.
     */
    public static TypesAggregate create(
            TypesBusinessUuId bizId,
            TypesName name,
            TypesPhysicalSpecs physicalSpecs,
            Actor creator) {

        return TypesAggregate.create(
                TypesUuId.generate(),
                bizId,
                name,
                physicalSpecs,
                creator
        );
    }

    /**
     * Rebuilds the aggregate from persistence state.
     * Includes the 'deleted' flag to restore the lifecycle status.
     */
    public static TypesAggregate reconstitute(
            TypesId id,
            TypesUuId uuId,
            TypesBusinessUuId bizId,
            TypesName name,
            TypesPhysicalSpecs physicalSpecs,
            boolean deleted,
            AuditMetadata audit) {

        return new TypesAggregate(
                id,
                uuId,
                bizId,
                name,
                physicalSpecs,
                deleted,
                audit
        );
    }
}

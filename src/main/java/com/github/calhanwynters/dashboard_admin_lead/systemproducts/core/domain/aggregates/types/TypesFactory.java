package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

public class TypesFactory {

    /**
     * Delegates to the Aggregate static factory to ensure the
     * TypeCreatedEvent is properly registered in the AbstractAggregateRoot.
     * The Aggregate factory internally initializes ProductBooleans(false, false).
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
     * Uses the ProductBooleans record to restore both archival and deletion status.
     */
    public static TypesAggregate reconstitute(
            TypesId id,
            TypesUuId uuId,
            TypesBusinessUuId bizId,
            TypesName name,
            TypesPhysicalSpecs physicalSpecs,
            ProductBooleans productBooleans, // Replaced boolean deleted
            AuditMetadata audit) {

        return new TypesAggregate(
                id,
                uuId,
                bizId,
                name,
                physicalSpecs,
                productBooleans,
                audit
        );
    }
}

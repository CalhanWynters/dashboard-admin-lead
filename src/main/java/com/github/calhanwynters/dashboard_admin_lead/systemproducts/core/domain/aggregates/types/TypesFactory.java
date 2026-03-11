package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleansLEGACY;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

public class TypesFactory {

    /**
     * Delegates to the Aggregate static factory to ensure the
     * TypeCreatedEvent is properly registered in the AbstractAggregateRoot.
     * The Aggregate factory internally initializes ProductBooleans(false, false).
     */
    public static TypesAggregateLEGACY create(
            TypesBusinessUuId bizId,
            TypesName name,
            TypesPhysicalSpecs physicalSpecs,
            Actor creator) {

        return TypesAggregateLEGACY.create(
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
    public static TypesAggregateLEGACY reconstitute(
            TypesId id,
            TypesUuId uuId,
            TypesBusinessUuId bizId,
            TypesName name,
            TypesPhysicalSpecs physicalSpecs,
            ProductBooleansLEGACY productBooleansLEGACY, // Replaced boolean deleted
            AuditMetadata audit) {

        return new TypesAggregateLEGACY(
                id,
                uuId,
                bizId,
                name,
                physicalSpecs,
                productBooleansLEGACY,
                audit
        );
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;

import java.util.Set;

public class TypeListFactory {

    /**
     * Delegates to the Aggregate's static factory to ensure the
     * TypeListCreatedEvent is properly registered.
     */
    public static TypeListAggregate create(TypeListBusinessUuId bizId, Actor creator) {
        // We delegate creation to the aggregate to capture the Domain Event
        return TypeListAggregate.create(
                TypeListUuId.generate(),
                bizId,
                creator
        );
    }

    /**
     * Used by the Infrastructure layer to rebuild an existing entity from the DB.
     * Note: Reconstitution bypasses domain events.
     */
    public static TypeListAggregate reconstitute(
            TypeListId id,
            TypeListUuId uuId,
            TypeListBusinessUuId bizId,
            Set<TypesUuId> ids,
            boolean deleted, // Added state for reconstitution
            AuditMetadata audit) {

        return new TypeListAggregate(
                id,
                uuId,
                bizId,
                ids,
                deleted, // 5th argument
                audit    // 6th argument
        );
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesUpdateBusUuIdDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;

/**
 * Assembler for updating a Product Type's Business UUID.
 * Ensures the change is attributed to the Actor for multi-tenancy audit trails.
 */
public final class TypesUpdateBusUuIdMapper {

    private TypesUpdateBusUuIdMapper() {
        // Private constructor for utility class
    }

    /**
     * Executes the business UUID update on the aggregate.
     * Triggers domain-level validation and emits the BusinessUuIdChanged event.
     */
    public static void mapToUpdate(TypesUpdateBusUuIdDTO dto, TypesAggregate aggregate) {
        if (dto == null || aggregate == null) {
            return;
        }

        // Aligned with TypesAggregate.updateBusinessUuId(TypesBusinessUuId, Actor)
        aggregate.updateBusinessUuId(
                dto.toTypesBusinessUuId(),
                dto.toActor()
        );
    }
}

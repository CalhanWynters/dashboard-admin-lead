package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesSoftDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;

/**
 * Assembler for soft-deleting a Product Type.
 * Connects the deletion DTO to the Aggregate's lifecycle logic.
 */
public final class TypesSoftMapper {

    private TypesSoftMapper() {
        // Private constructor for utility class
    }

    /**
     * Executes the soft-delete transition on the aggregate.
     * Ensures the deletion is attributed to the Actor for SOC 2 integrity.
     */
    public static void mapToSoftDelete(TypesSoftDTO dto, TypesAggregate aggregate) {
        if (dto == null || aggregate == null) {
            return;
        }

        // Bridges to aggregate.softDelete(Actor)
        aggregate.softDelete(dto.toActor());
    }
}

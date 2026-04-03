package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesArchiveDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;

/**
 * Assembler for archiving a Product Type.
 * Bridges the Archive DTO to the Domain Aggregate lifecycle logic.
 */
public final class TypesArchiveMapper {

    private TypesArchiveMapper() {
        // Private constructor for utility class
    }

    /**
     * Executes the archive transition on the aggregate using DTO context.
     * Ensures the operation is attributed to the correct Actor for SOC 2 compliance.
     */
    public static void mapToArchive(TypesArchiveDTO dto, TypesAggregate aggregate) {
        if (dto == null || aggregate == null) {
            return;
        }

        // Triggers the domain logic for state transition and event emission
        aggregate.archive(dto.toActor());
    }
}

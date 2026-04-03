package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.dto.VariantListArchiveDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListAggregate;

/**
 * Assembler for archiving a VariantList.
 * Bridges the Archive DTO to the Domain Aggregate lifecycle logic.
 */
public final class VariantListArchiveMapper {

    private VariantListArchiveMapper() {
        // Private constructor for utility class
    }

    /**
     * Executes the archive transition on the aggregate using DTO context.
     * Ensures the operation is attributed to the Actor for SOC 2 integrity.
     */
    public static void mapToArchive(VariantListArchiveDTO dto, VariantListAggregate aggregate) {
        if (dto == null || aggregate == null) {
            return;
        }

        // Triggers the domain logic for state transition and event emission
        aggregate.archive(dto.toActor());
    }
}

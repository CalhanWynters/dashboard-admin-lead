package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsArchiveDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;

/**
 * Assembler for archiving a Variant.
 * Bridges the Archive DTO to the Domain Aggregate lifecycle logic.
 */
public final class VariantsArchiveMapper {

    private VariantsArchiveMapper() {}

    /**
     * Executes the archive transition on the aggregate using DTO context.
     * Ensures the operation is attributed to the Actor for SOC 2 integrity.
     */
    public static void mapToArchive(VariantsArchiveDTO dto, VariantsAggregate aggregate) {
        if (dto == null || aggregate == null) return;

        // Triggers the domain logic for state transition and event emission
        aggregate.archive(dto.toActor());
    }
}

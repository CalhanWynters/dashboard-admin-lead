package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsEditNameDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;

/**
 * Assembler for updating a Variant's name.
 * Bridges high-precision DTO inputs to the hardened Domain Aggregate.
 */
public final class VariantsEditNameMapper {

    private VariantsEditNameMapper() {}

    /**
     * Applies the name change to the Aggregate.
     * Triggers DomainGuard validation and attributes the change to the Actor.
     */
    public static void mapToRename(VariantsEditNameDTO dto, VariantsAggregate aggregate) {
        if (dto == null || aggregate == null) return;

        // Aligned with VariantsAggregate.rename(VariantsName, Actor)
        aggregate.rename(
                dto.toVariantsName(),
                dto.toActor()
        );
    }
}

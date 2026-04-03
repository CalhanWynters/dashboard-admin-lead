package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypeEditPhysicalSpecsDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;

/**
 * Assembler for updating Type Physical Specifications.
 * Ensures the Domain Aggregate is updated using hardened DTO inputs.
 */
public final class TypeEditPhysicalSpecsMapper {

    private TypeEditPhysicalSpecsMapper() {}

    /**
     * Applies the DTO changes to the existing Aggregate.
     * This preserves the Aggregate's internal state while updating specs and attributing the actor.
     */
    public static void mapToAggregate(TypeEditPhysicalSpecsDTO dto, TypesAggregate aggregate) {
        if (dto == null || aggregate == null) {
            return;
        }

        // Triggers internal domain logic for specs update and SOC 2 auditing
        aggregate.updatePhysicalSpecs(
                dto.toTypesPhysicalSpecs(),
                dto.toActor()
        );
    }
}

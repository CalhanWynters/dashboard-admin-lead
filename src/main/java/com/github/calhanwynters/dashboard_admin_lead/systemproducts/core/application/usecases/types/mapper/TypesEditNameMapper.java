package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesEditNameDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;

/**
 * Assembler for updating a Product Type's name.
 * Bridges high-precision DTO inputs to the hardened Domain Aggregate.
 */
public final class TypesEditNameMapper {

    private TypesEditNameMapper() {
        // Private constructor for utility class
    }

    /**
     * Applies the name change to the Aggregate.
     * Triggers DomainGuard validation and attributes the change to the Actor.
     */
    public static void mapToNameUpdate(TypesEditNameDTO dto, TypesAggregate aggregate) {
        if (dto == null || aggregate == null) {
            return;
        }

        // Change updateName to rename to match the Aggregate's method signature
        aggregate.rename(
                dto.toTypesName(),
                dto.toActor()
        );
    }

}

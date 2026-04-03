package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.dto.VariantListUpdateBusUuIdDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListAggregate;

/**
 * Assembler for updating a VariantList's Business UUID.
 * Bridges high-precision DTO inputs to the hardened Domain Aggregate.
 */
public final class VariantListUpdateBusUuIdMapper {

    private VariantListUpdateBusUuIdMapper() {
        // Private constructor for utility class
    }

    /**
     * Executes the business UUID update on the aggregate.
     * Triggers domain-level validation and emits the VariantListBusinessUuIdChanged event.
     */
    public static void mapToUpdate(VariantListUpdateBusUuIdDTO dto, VariantListAggregate aggregate) {
        if (dto == null || aggregate == null) {
            return;
        }

        // Aligned with VariantListAggregate.updateBusinessUuId(VariantListBusinessUuId, Actor)
        aggregate.updateBusinessUuId(
                dto.toVariantListBusinessUuId(),
                dto.toActor()
        );
    }
}

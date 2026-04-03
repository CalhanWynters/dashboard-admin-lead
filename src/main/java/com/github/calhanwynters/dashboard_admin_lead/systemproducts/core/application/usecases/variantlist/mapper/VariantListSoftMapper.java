package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.dto.VariantListSoftDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListAggregate;

/**
 * Assembler for VariantList lifecycle transitions.
 * Bridges Soft-Delete/Restore DTOs to the hardened Domain Aggregate.
 */
public final class VariantListSoftMapper {

    private VariantListSoftMapper() {
        // Private constructor for utility class
    }

    /**
     * Executes the soft-delete transition on the aggregate.
     * Triggers LifecycleState change and VariantListSoftDeletedEvent.
     */
    public static void mapToSoftDelete(VariantListSoftDTO dto, VariantListAggregate aggregate) {
        if (dto == null || aggregate == null) return;

        aggregate.softDelete(dto.toActor());
    }

    /**
     * Executes the restoration transition on the aggregate.
     * Reverses the soft-delete and registers VariantListRestoredEvent.
     */
    public static void mapToRestore(VariantListSoftDTO dto, VariantListAggregate aggregate) {
        if (dto == null || aggregate == null) return;

        aggregate.restore(dto.toActor());
    }
}

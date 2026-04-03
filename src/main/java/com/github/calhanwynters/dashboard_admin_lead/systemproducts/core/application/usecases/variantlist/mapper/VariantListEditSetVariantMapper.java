package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.dto.VariantListEditSetVariantDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListAggregate;

/**
 * Assembler for modifying VariantList membership.
 * Bridges the EditSet DTO to granular attach/detach domain actions.
 */
public final class VariantListEditSetVariantMapper {

    private VariantListEditSetVariantMapper() {
        // Private constructor for utility class
    }

    /**
     * Executes the attachment of a Variant to the List.
     * Triggers VariantListBehavior.ensureCanAttach and VariantAttachedEvent.
     */
    public static void mapToAttachment(VariantListEditSetVariantDTO dto, VariantListAggregate aggregate) {
        if (dto == null || aggregate == null) return;

        aggregate.attachVariant(
                dto.toVariantsUuId(),
                dto.toActor()
        );
    }

    /**
     * Executes the detachment of a Variant from the List.
     * Triggers VariantListBehavior.ensureCanDetach and VariantDetachedEvent.
     */
    public static void mapToDetachment(VariantListEditSetVariantDTO dto, VariantListAggregate aggregate) {
        if (dto == null || aggregate == null) return;

        aggregate.detachVariant(
                dto.toVariantsUuId(),
                dto.toActor()
        );
    }
}

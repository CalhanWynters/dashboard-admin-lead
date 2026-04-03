package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsEditSetFeatureDTO;
import java.util.UUID;

/**
 * Command for unassigning a specific Feature from a Variant.
 * Formalizes the removal of variant capabilities for SOC 2 membership integrity.
 */
public record VariantsUnassignFeatureCommand(
        UUID targetUuId,
        VariantsEditSetFeatureDTO data
) {
    /**
     * Factory method to ensure the unassignment intent is fully qualified.
     */
    public static VariantsUnassignFeatureCommand of(UUID targetUuId, VariantsEditSetFeatureDTO data) {
        if (targetUuId == null || data == null) {
            throw new IllegalArgumentException("Target UUID and Feature Data are required for unassignment.");
        }
        return new VariantsUnassignFeatureCommand(targetUuId, data);
    }
}

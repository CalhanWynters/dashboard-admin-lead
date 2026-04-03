package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsEditSetFeatureDTO;
import java.util.UUID;

/**
 * Command for assigning a Feature to a Variant.
 * Formalizes the intent to modify variant capabilities for SOC 2 integrity.
 */
public record VariantsAssignFeatureCommand(
        UUID targetUuId,
        VariantsEditSetFeatureDTO data
) {
    /**
     * Factory method to ensure the assignment context is fully qualified.
     */
    public static VariantsAssignFeatureCommand of(UUID targetUuId, VariantsEditSetFeatureDTO data) {
        if (targetUuId == null || data == null) {
            throw new IllegalArgumentException("Target UUID and Feature Data are required.");
        }
        return new VariantsAssignFeatureCommand(targetUuId, data);
    }
}

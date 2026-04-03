package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsSoftDTO;
import java.util.UUID;

/**
 * Command for soft-deleting a Variant.
 * Essential for SOC 2 lifecycle management and data retention compliance.
 */
public record VariantsSoftDeleteCommand(
        UUID targetUuId,
        VariantsSoftDTO data
) {
    /**
     * Factory method to ensure the deletion intent is fully qualified.
     */
    public static VariantsSoftDeleteCommand of(UUID targetUuId, VariantsSoftDTO data) {
        if (targetUuId == null || data == null) {
            throw new IllegalArgumentException("Target UUID and Soft Delete data are required.");
        }
        return new VariantsSoftDeleteCommand(targetUuId, data);
    }
}

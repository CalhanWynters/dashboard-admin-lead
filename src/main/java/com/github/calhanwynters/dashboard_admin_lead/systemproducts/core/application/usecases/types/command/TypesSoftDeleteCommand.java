package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesSoftDTO;
import java.util.UUID;

/**
 * Command for soft-deleting a Product Type.
 * Essential for SOC 2 lifecycle management and data retention compliance.
 */
public record TypesSoftDeleteCommand(
        UUID targetUuId,
        TypesSoftDTO data
) {
    /**
     * Factory method to ensure the deletion intent is fully qualified.
     */
    public static TypesSoftDeleteCommand of(UUID targetUuId, TypesSoftDTO data) {
        if (targetUuId == null || data == null) {
            throw new IllegalArgumentException("Target UUID and Soft Delete data are required.");
        }
        return new TypesSoftDeleteCommand(targetUuId, data);
    }
}

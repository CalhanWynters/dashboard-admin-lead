package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesArchiveDTO;
import java.util.UUID;

/**
 * Command for archiving a Product Type.
 * Decouples the API request (DTO) from the Use Case execution context.
 */
public record TypesArchiveCommand(
        UUID targetUuId,
        TypesArchiveDTO data
) {
    /**
     * Factory method to initialize from a request context.
     * Ensures the target UUID is present before reaching the Use Case.
     */
    public static TypesArchiveCommand of(UUID targetUuId, TypesArchiveDTO data) {
        if (targetUuId == null || data == null) {
            throw new IllegalArgumentException("Target UUID and Archive Data are required.");
        }
        return new TypesArchiveCommand(targetUuId, data);
    }
}

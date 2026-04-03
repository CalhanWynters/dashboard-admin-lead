package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesEditNameDTO;
import java.util.UUID;

/**
 * Command for renaming a Product Type.
 * Maps the target resource to the name-change payload for SOC 2 integrity.
 */
public record TypesRenameCommand(
        UUID targetUuId,
        TypesEditNameDTO data
) {
    /**
     * Factory method to ensure the renaming context is fully qualified.
     */
    public static TypesRenameCommand of(UUID targetUuId, TypesEditNameDTO data) {
        if (targetUuId == null || data == null) {
            throw new IllegalArgumentException("Target UUID and Name Data are required.");
        }
        return new TypesRenameCommand(targetUuId, data);
    }
}

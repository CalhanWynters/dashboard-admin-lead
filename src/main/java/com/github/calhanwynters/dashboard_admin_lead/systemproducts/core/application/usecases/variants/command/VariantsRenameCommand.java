package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsEditNameDTO;
import java.util.UUID;

/**
 * Command for renaming a Variant.
 * Pairs the target identity with the high-precision name payload for SOC 2 integrity.
 */
public record VariantsRenameCommand(
        UUID targetUuId,
        VariantsEditNameDTO data
) {
    /**
     * Factory method to ensure the renaming intent is fully qualified.
     */
    public static VariantsRenameCommand of(UUID targetUuId, VariantsEditNameDTO data) {
        if (targetUuId == null || data == null) {
            throw new IllegalArgumentException("Target UUID and Name Data are required.");
        }
        return new VariantsRenameCommand(targetUuId, data);
    }
}

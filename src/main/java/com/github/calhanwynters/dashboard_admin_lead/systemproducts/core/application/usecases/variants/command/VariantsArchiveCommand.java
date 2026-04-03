package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsArchiveDTO;
import java.util.UUID;

/**
 * Command for archiving a Variant.
 * Pairs the target identity with the authorized actor context for SOC 2 compliance.
 */
public record VariantsArchiveCommand(
        UUID targetUuId,
        VariantsArchiveDTO data
) {
    /**
     * Factory method to ensure the archiving intent is fully qualified.
     */
    public static VariantsArchiveCommand of(UUID targetUuId, VariantsArchiveDTO data) {
        if (targetUuId == null || data == null) {
            throw new IllegalArgumentException("Target UUID and Archive Data are required.");
        }
        return new VariantsArchiveCommand(targetUuId, data);
    }
}

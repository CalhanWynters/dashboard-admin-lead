package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsRegionDTO;
import java.util.UUID;

/**
 * Command for a Variant Region update.
 * Wraps the target identifier and the raw data payload for the handler.
 */
public record VariantsRegionUpdateCommand(
        UUID targetUuId,
        VariantsRegionDTO data
) {
    public static VariantsRegionUpdateCommand of(UUID targetUuId, VariantsRegionDTO data) {
        return new VariantsRegionUpdateCommand(targetUuId, data);
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsUpdateBusUuIdDTO;
import java.util.UUID;

/**
 * Command representing the intent to update a Variant's Business UUID.
 * Handled by VariantsUpdateBusUuIdHandler for multi-tenant ownership changes.
 */
public record VariantsUpdateBusUuIdCommand(
        UUID targetUuId,
        VariantsUpdateBusUuIdDTO data
) {
    /**
     * Factory method to ensure the update context is fully qualified.
     */
    public static VariantsUpdateBusUuIdCommand of(UUID targetUuId, VariantsUpdateBusUuIdDTO data) {
        if (targetUuId == null || data == null) {
            throw new IllegalArgumentException("Target UUID and Business Update Data are required.");
        }
        return new VariantsUpdateBusUuIdCommand(targetUuId, data);
    }
}

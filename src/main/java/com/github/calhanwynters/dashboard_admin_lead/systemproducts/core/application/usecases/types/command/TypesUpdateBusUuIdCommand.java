package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesUpdateBusUuIdDTO;
import java.util.UUID;

/**
 * Command for updating a Product Type's Business UUID.
 * Formalizes the intent to change organizational affiliation for SOC 2 isolation.
 */
public record TypesUpdateBusUuIdCommand(
        UUID targetUuId,
        TypesUpdateBusUuIdDTO data
) {
    /**
     * Factory method to ensure the update context is fully qualified.
     */
    public static TypesUpdateBusUuIdCommand of(UUID targetUuId, TypesUpdateBusUuIdDTO data) {
        if (targetUuId == null || data == null) {
            throw new IllegalArgumentException("Target UUID and Business Update Data are required.");
        }
        return new TypesUpdateBusUuIdCommand(targetUuId, data);
    }
}

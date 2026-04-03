package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypeEditPhysicalSpecsDTO;
import java.util.UUID;

/**
 * Command for updating Product Type Physical Specifications.
 * Maps the target resource to high-precision spec data for SOC 2 integrity.
 */
public record TypeUpdatePhysicalSpecsCommand(
        UUID targetUuId,
        TypeEditPhysicalSpecsDTO data
) {
    /**
     * Factory method to ensure the spec update context is fully qualified.
     */
    public static TypeUpdatePhysicalSpecsCommand of(UUID targetUuId, TypeEditPhysicalSpecsDTO data) {
        if (targetUuId == null || data == null) {
            throw new IllegalArgumentException("Target UUID and Physical Specs data are required.");
        }
        return new TypeUpdatePhysicalSpecsCommand(targetUuId, data);
    }
}

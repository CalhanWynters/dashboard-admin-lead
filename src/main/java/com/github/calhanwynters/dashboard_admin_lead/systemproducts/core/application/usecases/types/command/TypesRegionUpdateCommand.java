package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesRegionDTO;

import java.util.UUID;

public record TypesRegionUpdateCommand(
        UUID targetUuId,
        TypesRegionDTO data
) {
    public static TypesRegionUpdateCommand of(UUID targetUuId, TypesRegionDTO data) {
        return new TypesRegionUpdateCommand(targetUuId, data);
    }
}
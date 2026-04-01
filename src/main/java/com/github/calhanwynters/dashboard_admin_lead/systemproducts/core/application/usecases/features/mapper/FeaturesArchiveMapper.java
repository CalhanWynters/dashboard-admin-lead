package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeaturesArchiveDTO;
import java.util.Set;

/**
 * Mapper for Archive operations.
 * Bridges the gap between API Request DTOs and Domain Security Contexts.
 */
public final class FeaturesArchiveMapper {

    private FeaturesArchiveMapper() {} // Static utility only

    /**
     * Map the Archive DTO to a Domain Actor.
     * Enforces a non-null Role set for safe role-checking in the Aggregate.
     */
    public static Actor toActor(FeaturesArchiveDTO dto) {
        return Actor.of(
                dto.actorId(),
                dto.actorRoles() != null ? dto.actorRoles() : Set.of()
        );
    }
}

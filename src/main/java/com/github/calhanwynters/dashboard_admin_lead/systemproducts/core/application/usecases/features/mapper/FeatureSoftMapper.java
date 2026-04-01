package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto.FeatureSoftDTO;
import java.util.Set;

/**
 * Mapper for Soft Delete operations.
 * Centralizes the transformation of Actor context for lifecycle changes.
 */
public final class FeatureSoftMapper {

    private FeatureSoftMapper() {} // Static utility only

    /**
     * Map the Soft Delete DTO to a Domain Actor.
     */
    public static Actor toActor(FeatureSoftDTO dto) {
        return Actor.of(
                dto.actorId(),
                dto.actorRoles() != null ? dto.actorRoles() : Set.of()
        );
    }
}

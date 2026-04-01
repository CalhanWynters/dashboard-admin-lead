package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto.ImagesSoftDTO;
import java.util.Set;

/**
 * Mapper for Image Soft-Delete and Restore operations.
 * Bridges API Request DTOs to Domain Security Contexts.
 */
public final class ImagesSoftMapper {

    private ImagesSoftMapper() {} // Static utility only

    /**
     * Map the Soft Delete DTO to a Domain Actor.
     * Defaults to an empty set for roles to satisfy SOC 2 authorization checks.
     */
    public static Actor toActor(ImagesSoftDTO dto) {
        return Actor.of(
                dto.actorId(),
                dto.actorRoles() != null ? dto.actorRoles() : Set.of()
        );
    }
}

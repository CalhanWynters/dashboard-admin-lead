package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.Set;

/**
 * DTO for toggling Gallery public visibility.
 * Captures the target status and the Actor context for SOC 2 authorization.
 */
public record GalleryTogglePublicStatusDTO(
        boolean newPublicStatus,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps to the common Actor record for domain logic authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

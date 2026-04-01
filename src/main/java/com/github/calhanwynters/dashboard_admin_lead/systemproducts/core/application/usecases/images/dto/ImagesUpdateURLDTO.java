package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUrl;

import java.util.Set;

/**
 * DTO for updating an Image's Source URL.
 * Maps raw input to hardened Domain URLs and Actor context.
 */
public record ImagesUpdateURLDTO(
        String newUrl,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened ImageUrl.
     * (Assumes ImageUrl record handles its own validation or wraps a URL VO).
     */
    public ImageUrl toImageUrl() {
        return new ImageUrl(newUrl);
    }

    /**
     * Reconstructs the Actor for aggregate authorization and auditing.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

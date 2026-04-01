package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.Description;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageDescription;

import java.util.Set;

/**
 * DTO for updating an Image's Description.
 * Maps raw text to hardened Domain Descriptions and Actor context.
 */
public record ImagesUpdateDescriptionDTO(
        String newDescription,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened ImageDescription.
     * Triggers DomainGuard validation for length, lexical content, and DoS safety.
     */
    public ImageDescription toImageDescription() {
        return new ImageDescription(Description.from(newDescription));
    }

    /**
     * Reconstructs the Actor for aggregate authorization and auditing.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

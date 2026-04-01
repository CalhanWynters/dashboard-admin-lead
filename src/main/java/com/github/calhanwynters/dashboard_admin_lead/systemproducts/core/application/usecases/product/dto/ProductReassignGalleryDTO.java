package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.product.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

import java.util.Set;

/**
 * DTO for reassigning a Gallery to a Product.
 * Maps raw UUID input to hardened Gallery identifiers and Actor context.
 */
public record ProductReassignGalleryDTO(
        String newGalleryUuid,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened GalleryUuId.
     * Triggers DomainGuard validation for UUID syntax.
     */
    public GalleryUuId toGalleryUuId() {
        return new GalleryUuId(UuId.fromString(newGalleryUuid));
    }

    /**
     * Reconstructs the Actor for aggregate authorization and auditing.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

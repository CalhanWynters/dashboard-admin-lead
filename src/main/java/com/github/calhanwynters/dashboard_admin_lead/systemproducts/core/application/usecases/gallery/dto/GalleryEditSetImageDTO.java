package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

import java.util.Set;

/**
 * DTO for modifying the Image set within a Gallery.
 * Maps raw UUID strings to hardened ImageUuId records.
 */
public record GalleryEditSetImageDTO(
        String imageUuid,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened ImageUuId.
     * Triggers DomainGuard validation for UUID syntax.
     */
    public ImageUuId toImageUuId() {
        return new ImageUuId(UuId.fromString(imageUuid));
    }

    /**
     * Maps to the common Actor record for domain logic authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

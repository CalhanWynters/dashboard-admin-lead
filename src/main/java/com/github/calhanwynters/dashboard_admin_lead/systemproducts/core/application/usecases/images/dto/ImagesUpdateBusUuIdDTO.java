package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImagesBusinessUuId;

import java.util.Set;

/**
 * DTO for updating an Image's Business UUID.
 * Maps raw input to hardened Business Identifiers and Actor context.
 */
public record ImagesUpdateBusUuIdDTO(
        String newBusinessUuid,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened ImagesBusinessUuId.
     * Triggers DomainGuard validation for UUID syntax and length.
     */
    public ImagesBusinessUuId toImagesBusinessUuId() {
        return new ImagesBusinessUuId(UuId.fromString(newBusinessUuid));
    }

    /**
     * Maps to the common Actor record for domain logic authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

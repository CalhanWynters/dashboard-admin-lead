package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageName;

import java.util.Set;

/**
 * DTO for renaming an Image.
 * Maps raw input to hardened Domain Names and Actor context.
 */
public record ImagesRenameDTO(
        String newName,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened ImageName.
     * Triggers DomainGuard validation for length and lexical content.
     */
    public ImageName toImageName() {
        return new ImageName(Name.from(newName));
    }

    /**
     * Maps to the common Actor record for domain logic authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

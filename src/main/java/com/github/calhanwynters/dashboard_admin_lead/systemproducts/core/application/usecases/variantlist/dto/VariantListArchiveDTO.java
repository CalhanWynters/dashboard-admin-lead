package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.Set;

/**
 * DTO for archiving a VariantList.
 * Captures the Actor identity and roles for lifecycle authorization.
 */
public record VariantListArchiveDTO(
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

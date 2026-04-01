package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.Set;

/**
 * DTO for soft-deleting an individual Variant.
 * Captures the Actor identity and roles for lifecycle authorization.
 */
public record VariantsSoftDTO(
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

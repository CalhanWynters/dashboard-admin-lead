package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.Set;

/**
 * DTO for archiving a Feature.
 * Captures the Actor context required for SOC 2 lifecycle authorization.
 */
public record FeaturesArchiveDTO(
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Reconstructs the Actor for the aggregate's archive(actor) method.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.Set;
import java.util.UUID;

/**
 * Command for purging all assigned Features from a Variant.
 * Handled by VariantsUnassignAllFeaturesHandler for bulk membership modification.
 */
public record VariantsUnassignAllFeaturesCommand(
        UUID targetUuId,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Factory method to ensure the bulk unassignment intent is fully qualified.
     */
    public static VariantsUnassignAllFeaturesCommand of(UUID targetUuId, String actorId, Set<String> actorRoles) {
        if (targetUuId == null || actorId == null) {
            throw new IllegalArgumentException("Target UUID and Actor ID are required for bulk unassignment.");
        }
        return new VariantsUnassignAllFeaturesCommand(targetUuId, actorId, actorRoles != null ? actorRoles : Set.of());
    }

    /**
     * Reconstructs the Actor for domain-level authority verification.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles);
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.Set;
import java.util.UUID;

/**
 * Command for unarchiving a Product Type.
 * Restores the aggregate to an active state for SOC 2 lifecycle management.
 */
public record TypesUnArchiveCommand(
        UUID targetUuId,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Factory method to ensure the unarchive intent is fully qualified.
     */
    public static TypesUnArchiveCommand of(UUID targetUuId, String actorId, Set<String> actorRoles) {
        if (targetUuId == null || actorId == null) {
            throw new IllegalArgumentException("Target UUID and Actor ID are required for unarchiving.");
        }
        return new TypesUnArchiveCommand(targetUuId, actorId, actorRoles != null ? actorRoles : Set.of());
    }

    /**
     * Reconstructs the Actor for the unarchive transition and event attribution.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles);
    }
}

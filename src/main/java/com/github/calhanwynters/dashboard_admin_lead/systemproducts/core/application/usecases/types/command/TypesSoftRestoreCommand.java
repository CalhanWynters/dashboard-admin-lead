package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.Set;
import java.util.UUID;

/**
 * Command for restoring a soft-deleted Product Type.
 * Critical for SOC 2 data recovery and lifecycle accountability.
 */
public record TypesSoftRestoreCommand(
        UUID targetUuId,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Factory method to ensure the restoration intent is fully qualified.
     */
    public static TypesSoftRestoreCommand of(UUID targetUuId, String actorId, Set<String> actorRoles) {
        if (targetUuId == null || actorId == null) {
            throw new IllegalArgumentException("Target UUID and Actor ID are required for restoration.");
        }
        return new TypesSoftRestoreCommand(targetUuId, actorId, actorRoles != null ? actorRoles : Set.of());
    }

    /**
     * Reconstructs the Actor for domain-level authorization during the restore transition.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles);
    }
}

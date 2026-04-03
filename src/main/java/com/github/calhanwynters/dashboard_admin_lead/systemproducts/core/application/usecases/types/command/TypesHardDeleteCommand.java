package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.Set;
import java.util.UUID;

/**
 * Command for the permanent removal of a Product Type.
 * Critical for SOC 2 Data Disposal compliance.
 */
public record TypesHardDeleteCommand(
        UUID targetUuId,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Factory method to ensure the destructive intent is fully qualified.
     */
    public static TypesHardDeleteCommand of(UUID targetUuId, String actorId, Set<String> actorRoles) {
        if (targetUuId == null || actorId == null) {
            throw new IllegalArgumentException("Target UUID and Actor ID are required for hard deletion.");
        }
        return new TypesHardDeleteCommand(targetUuId, actorId, actorRoles != null ? actorRoles : Set.of());
    }

    /**
     * Reconstructs the Actor for final authorization check in the Use Case.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles);
    }
}

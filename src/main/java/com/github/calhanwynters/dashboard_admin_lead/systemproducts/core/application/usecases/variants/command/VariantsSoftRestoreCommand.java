package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.Set;
import java.util.UUID;

/**
 * Command representing the intent to restore a soft-deleted Variant.
 * Handled by VariantsSoftRestoreHandler for SOC 2 lifecycle accountability.
 */
public record VariantsSoftRestoreCommand(
        UUID targetUuId,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Factory method to ensure the restoration intent is fully qualified.
     */
    public static VariantsSoftRestoreCommand of(UUID targetUuId, String actorId, Set<String> actorRoles) {
        if (targetUuId == null || actorId == null) {
            throw new IllegalArgumentException("Target UUID and Actor ID are required for restoration.");
        }
        return new VariantsSoftRestoreCommand(targetUuId, actorId, actorRoles != null ? actorRoles : Set.of());
    }

    /**
     * Reconstructs the Actor for domain-level authorization during the restore transition.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles);
    }
}

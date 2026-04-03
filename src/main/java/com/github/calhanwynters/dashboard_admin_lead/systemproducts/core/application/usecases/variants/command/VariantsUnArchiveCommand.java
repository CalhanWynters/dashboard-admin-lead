package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.Set;
import java.util.UUID;

/**
 * Command representing the intent to unarchive a Variant.
 * Handled by VariantsUnArchiveHandler to restore active management of the variant.
 */
public record VariantsUnArchiveCommand(
        UUID targetUuId,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Factory method to ensure the unarchive intent is fully qualified.
     */
    public static VariantsUnArchiveCommand of(UUID targetUuId, String actorId, Set<String> actorRoles) {
        if (targetUuId == null || actorId == null) {
            throw new IllegalArgumentException("Target UUID and Actor ID are required for unarchiving.");
        }
        return new VariantsUnArchiveCommand(targetUuId, actorId, actorRoles != null ? actorRoles : Set.of());
    }

    /**
     * Reconstructs the Actor for the unarchive transition and event attribution.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles);
    }
}

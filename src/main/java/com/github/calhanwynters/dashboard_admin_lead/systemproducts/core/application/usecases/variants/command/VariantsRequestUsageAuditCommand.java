package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.Set;
import java.util.UUID;

/**
 * Command for requesting a usage audit of a Variant.
 * Triggers a pure domain event for SOC 2 discovery without mutating state.
 */
public record VariantsRequestUsageAuditCommand(
        UUID targetUuId,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Factory method to ensure the audit request is fully qualified.
     */
    public static VariantsRequestUsageAuditCommand of(UUID targetUuId, String actorId, Set<String> actorRoles) {
        if (targetUuId == null || actorId == null) {
            throw new IllegalArgumentException("Target UUID and Actor ID are required for audit requests.");
        }
        return new VariantsRequestUsageAuditCommand(targetUuId, actorId, actorRoles != null ? actorRoles : Set.of());
    }

    /**
     * Reconstructs the Actor for domain-level authority verification.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles);
    }
}

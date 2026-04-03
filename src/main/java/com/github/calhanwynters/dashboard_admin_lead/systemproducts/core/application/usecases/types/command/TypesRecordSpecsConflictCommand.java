package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.Set;
import java.util.UUID;

/**
 * Command for recording a detected physical specification conflict.
 * Essential for SOC 2 automated integrity auditing and discrepancy tracking.
 */
public record TypesRecordSpecsConflictCommand(
        UUID targetUuId,
        String conflictDetails,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Factory method to ensure the audit record is fully qualified.
     */
    public static TypesRecordSpecsConflictCommand of(
            UUID targetUuId,
            String details,
            String actorId,
            Set<String> actorRoles
    ) {
        if (targetUuId == null || details == null || actorId == null) {
            throw new IllegalArgumentException("Target UUID, details, and Actor ID are required.");
        }
        return new TypesRecordSpecsConflictCommand(
                targetUuId,
                details,
                actorId,
                actorRoles != null ? actorRoles : Set.of()
        );
    }

    /**
     * Reconstructs the Actor (often Actor.SYSTEM) for domain-level authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles);
    }
}

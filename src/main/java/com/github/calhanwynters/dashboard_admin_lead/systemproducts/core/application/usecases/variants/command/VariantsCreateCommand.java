package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsDTO;
import java.util.Set;

/**
 * Command for creating a new Variant.
 * Encapsulates initial state and Actor context for aggregate instantiation.
 */
public record VariantsCreateCommand(
        VariantsDTO data,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Factory method to ensure all required creation context is present.
     */
    public static VariantsCreateCommand of(VariantsDTO data, String actorId, Set<String> actorRoles) {
        if (data == null || actorId == null) {
            throw new IllegalArgumentException("Creation data and Actor ID are required.");
        }
        return new VariantsCreateCommand(data, actorId, actorRoles != null ? actorRoles : Set.of());
    }

    /**
     * Helper to reconstruct the Actor for the Domain Factory.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles);
    }
}

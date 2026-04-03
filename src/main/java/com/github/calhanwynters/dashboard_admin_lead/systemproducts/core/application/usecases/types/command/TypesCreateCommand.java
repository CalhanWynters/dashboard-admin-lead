package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.command;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesDTO;

/**
 * Command for creating a new Product Type.
 * Encapsulates the initial state and actor context for aggregate instantiation.
 */
public record TypesCreateCommand(
        TypesDTO data,
        String actorId,
        java.util.Set<String> actorRoles
) {
    /**
     * Factory method to ensure all required creation context is present.
     */
    public static TypesCreateCommand of(TypesDTO data, String actorId, java.util.Set<String> actorRoles) {
        if (data == null || actorId == null) {
            throw new IllegalArgumentException("Creation data and Actor ID are required.");
        }
        return new TypesCreateCommand(data, actorId, actorRoles != null ? actorRoles : java.util.Set.of());
    }

    /**
     * Helper to reconstruct the Actor for the Domain Factory.
     */
    public com.github.calhanwynters.dashboard_admin_lead.common.Actor toActor() {
        return com.github.calhanwynters.dashboard_admin_lead.common.Actor.of(actorId, actorRoles);
    }
}

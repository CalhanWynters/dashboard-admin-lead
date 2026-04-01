package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesName;

import java.util.Set;

/**
 * DTO for editing a Type's Name.
 * Maps raw input to hardened Domain Names and Actor context.
 */
public record TypesEditNameDTO(
        String newName,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened TypesName.
     * Triggers DomainGuard validation for length and lexical content.
     */
    public TypesName toTypesName() {
        return new TypesName(Name.from(newName));
    }

    /**
     * Reconstructs the Actor for aggregate authorization and auditing.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

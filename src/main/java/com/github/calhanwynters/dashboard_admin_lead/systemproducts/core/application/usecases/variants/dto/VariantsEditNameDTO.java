package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsName;

import java.util.Set;

/**
 * DTO for editing a Variant's Name.
 * Maps raw input to hardened Domain Names and Actor context.
 */
public record VariantsEditNameDTO(
        String newName,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened VariantsName.
     * Triggers DomainGuard validation for length and lexical content.
     */
    public VariantsName toVariantsName() {
        return new VariantsName(Name.from(newName));
    }

    /**
     * Reconstructs the Actor for aggregate authorization and auditing.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

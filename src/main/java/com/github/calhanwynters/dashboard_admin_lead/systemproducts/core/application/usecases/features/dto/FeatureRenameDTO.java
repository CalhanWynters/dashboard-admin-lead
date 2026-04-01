package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureName;

import java.util.Set;

/**
 * DTO for renaming a Feature.
 * Captures the new name and actor context for the rename operation.
 */
public record FeatureRenameDTO(
        String newName,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps to the domain FeatureName wrapper.
     */
    public FeatureName toFeatureName() {
        return new FeatureName(Name.from(newName));
    }

    /**
     * Maps to the common Actor record for domain logic authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}
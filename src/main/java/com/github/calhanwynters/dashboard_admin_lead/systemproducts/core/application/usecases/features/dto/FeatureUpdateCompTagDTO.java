package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.Label;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureLabel;

import java.util.Set;

/**
 * DTO for updating a Feature's Compatibility Tag.
 * Maps raw input to hardened Domain Labels and Actor context.
 */
public record FeatureUpdateCompTagDTO(
        String newTag,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened FeatureLabel.
     * Triggers DomainGuard validation for length and lexical content.
     */
    public FeatureLabel toFeatureLabel() {
        return new FeatureLabel(Label.from(newTag));
    }

    /**
     * Reconstructs the Actor for aggregate authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

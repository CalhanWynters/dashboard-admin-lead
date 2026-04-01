package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;

import java.util.Set;

/**
 * DTO for assigning or unassigning a Feature from a Variant.
 * Maps raw UUID input to hardened FeatureUuId records and Actor context.
 */
public record VariantsEditSetFeatureDTO(
        String featureUuid,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened FeatureUuId.
     * Triggers DomainGuard validation for UUID syntax and length.
     */
    public FeatureUuId toFeatureUuId() {
        return new FeatureUuId(UuId.fromString(featureUuid));
    }

    /**
     * Maps to the common Actor record for domain logic authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

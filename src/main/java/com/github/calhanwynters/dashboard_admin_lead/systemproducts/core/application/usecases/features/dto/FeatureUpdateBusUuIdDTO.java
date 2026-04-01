package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureBusinessUuId;

import java.util.Set;

/**
 * DTO for updating a Feature's Business UUID.
 * Captures the new identifier and the Actor context for SOC 2 authorization.
 */
public record FeatureUpdateBusUuIdDTO(
        String newBusinessUuid,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened FeatureBusinessUuId.
     * Triggers DomainGuard validation for UUID syntax and length.
     */
    public FeatureBusinessUuId toFeatureBusinessUuId() {
        return new FeatureBusinessUuId(UuId.fromString(newBusinessUuid));
    }

    /**
     * Maps to the common Actor record for domain logic authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

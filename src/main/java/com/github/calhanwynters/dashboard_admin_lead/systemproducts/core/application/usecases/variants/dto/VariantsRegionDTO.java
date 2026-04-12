package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.Region;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;

import java.util.Set;

/**
 * Command DTO for updating a Variant's Region.
 * Bridges raw API input to hardened Domain Objects.
 */
public record VariantsRegionDTO(
        String newRegionValue,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Converts to Domain Object.
     * This will trigger the DomainGuard checks in the Region record.
     */
    public VariantsRegion toVariantsRegion() {
        return VariantsRegion.from(Region.from(newRegionValue));
    }

    /**
     * Reconstructs the Actor for auth checks.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

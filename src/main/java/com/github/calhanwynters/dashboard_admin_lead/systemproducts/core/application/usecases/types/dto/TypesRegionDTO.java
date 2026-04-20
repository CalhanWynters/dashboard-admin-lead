package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.Region;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

import java.util.Set;

public record TypesRegionDTO(String newRegionValue,
                             String actorId,
                             Set<String> actorRoles
) {
    /**
     * Converts to Domain Object.
     * This will trigger the DomainGuard checks in the Region record.
     */
    public TypesRegion toTypesRegion() {
        return TypesRegion.from(Region.from(newRegionValue));
    }

    /**
     * Reconstructs the Actor for auth checks.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

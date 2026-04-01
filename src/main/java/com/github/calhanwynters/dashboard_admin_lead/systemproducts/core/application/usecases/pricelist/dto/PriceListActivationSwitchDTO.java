package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.Set;

/**
 * DTO for toggling a PriceList's operational activation status.
 * Captures the target state and Actor context for SOC 2 authorization.
 */
public record PriceListActivationSwitchDTO(
        boolean active,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps to the common Actor record for domain logic authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

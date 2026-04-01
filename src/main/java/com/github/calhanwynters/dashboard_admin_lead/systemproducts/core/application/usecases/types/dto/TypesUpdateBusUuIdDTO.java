package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesBusinessUuId;

import java.util.Set;

/**
 * DTO for updating a Type's Business UUID.
 * Maps raw input to hardened Business Identifiers and Actor context.
 */
public record TypesUpdateBusUuIdDTO(
        String newBusinessUuid,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened TypesBusinessUuId.
     * Triggers DomainGuard validation for UUID syntax and length.
     */
    public TypesBusinessUuId toTypesBusinessUuId() {
        return new TypesBusinessUuId(UuId.fromString(newBusinessUuid));
    }

    /**
     * Maps to the common Actor record for domain logic authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

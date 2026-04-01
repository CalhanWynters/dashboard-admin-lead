package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListBusinessUuId;

import java.util.Set;

/**
 * DTO for updating a VariantList's Business UUID.
 * Maps raw input to hardened Business Identifiers and Actor context.
 */
public record VariantListUpdateBusUuIdDTO(
        String newBusinessUuid,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened VariantListBusinessUuId.
     * Triggers DomainGuard validation for UUID syntax and length.
     */
    public VariantListBusinessUuId toVariantListBusinessUuId() {
        return new VariantListBusinessUuId(UuId.fromString(newBusinessUuid));
    }

    /**
     * Maps to the common Actor record for domain logic authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListBusinessUuId;

import java.util.Set;

/**
 * DTO for updating a PriceList's Business UUID.
 * Maps raw input to hardened Business Identifiers and Actor context.
 */
public record PriceListUpdateBusUuIdDTO(
        String newBusinessUuid,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened PriceListBusinessUuId.
     * Triggers DomainGuard validation for UUID syntax and length.
     */
    public PriceListBusinessUuId toPriceListBusinessUuId() {
        return new PriceListBusinessUuId(UuId.fromString(newBusinessUuid));
    }

    /**
     * Maps to the common Actor record for domain logic authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

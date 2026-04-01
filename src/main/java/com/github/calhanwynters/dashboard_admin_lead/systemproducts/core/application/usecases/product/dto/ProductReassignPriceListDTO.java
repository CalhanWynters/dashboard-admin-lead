package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.product.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

import java.util.Set;

/**
 * DTO for reassigning a PriceList to a Product.
 * Maps raw UUID input to hardened PriceList identifiers and Actor context.
 */
public record ProductReassignPriceListDTO(
        String newPriceListUuid,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened PriceListUuId.
     * Triggers DomainGuard validation for UUID syntax and format.
     */
    public PriceListUuId toPriceListUuId() {
        return new PriceListUuId(UuId.fromString(newPriceListUuid));
    }

    /**
     * Reconstructs the Actor for aggregate authorization and auditing.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

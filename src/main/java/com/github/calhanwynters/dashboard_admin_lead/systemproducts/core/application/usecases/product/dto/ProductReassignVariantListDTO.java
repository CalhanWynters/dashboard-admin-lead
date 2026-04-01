package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.product.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;

import java.util.Set;

/**
 * DTO for reassigning a VariantList to a Product.
 * Maps raw UUID input to hardened VariantList identifiers and Actor context.
 */
public record ProductReassignVariantListDTO(
        String newVariantListUuid,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened VariantListUuId.
     * Triggers DomainGuard validation for UUID syntax and format.
     */
    public VariantListUuId toVariantListUuId() {
        return new VariantListUuId(UuId.fromString(newVariantListUuid));
    }

    /**
     * Reconstructs the Actor for aggregate authorization and auditing.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.product.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;

import java.util.Set;

/**
 * DTO for reassigning a TypeList to a Product.
 * Maps raw UUID input to hardened TypeList identifiers and Actor context.
 */
public record ProductReassignTypeListDTO(
        String newTypeListUuid,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened TypeListUuId.
     * Triggers DomainGuard validation for UUID syntax and format.
     */
    public TypeListUuId toTypeListUuId() {
        return new TypeListUuId(UuId.fromString(newTypeListUuid));
    }

    /**
     * Reconstructs the Actor for aggregate authorization and auditing.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

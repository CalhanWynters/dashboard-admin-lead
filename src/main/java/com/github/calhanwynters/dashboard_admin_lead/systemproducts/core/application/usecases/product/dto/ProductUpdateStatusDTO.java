package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.product.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.StatusEnums;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.ProductStatus;

import java.util.Set;

/**
 * DTO for updating a Product's lifecycle status.
 * Maps raw string input to hardened Domain Status and Actor context.
 */
public record ProductUpdateStatusDTO(
        String newStatus,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened ProductStatus.
     * (Assumes ProductStatus wraps a StatusEnums value).
     */
    public ProductStatus toProductStatus() {
        return new ProductStatus(StatusEnums.valueOf(newStatus.toUpperCase()));
    }

    /**
     * Reconstructs the Actor for aggregate authorization and auditing.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

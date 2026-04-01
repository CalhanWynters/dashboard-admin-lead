package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.product.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.ProductThumbnailUrl;

import java.util.Set;

/**
 * DTO for updating a Product's Thumbnail URL.
 * Maps raw string input to hardened Domain records and Actor context.
 */
public record ProductUpdateThumbnailDTO(
        String newThumbnailUrl,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened ProductThumbnailUrl.
     * (Assumes ProductThumbnailUrl wraps a String or URL value object).
     */
    public ProductThumbnailUrl toProductThumbnailUrl() {
        return new ProductThumbnailUrl(newThumbnailUrl);
    }

    /**
     * Reconstructs the Actor for aggregate authorization and auditing.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

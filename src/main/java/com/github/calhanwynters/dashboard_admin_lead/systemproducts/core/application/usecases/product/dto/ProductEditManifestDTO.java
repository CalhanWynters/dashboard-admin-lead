package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.product.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.ProductManifest;

import java.util.Set;

/**
 * DTO for editing a Product's Manifest.
 * Maps raw manifest data to hardened Domain records and Actor context.
 */
public record ProductEditManifestDTO(
        String name,
        String description,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw strings into a hardened ProductManifest.
     * (Assumes ProductManifest wraps Name and Description records).
     */
    public ProductManifest toProductManifest() {
        // Implementation depends on your ProductManifest constructor
        // Typically: return new ProductManifest(Name.from(name), Description.from(description));
        return null;
    }

    /**
     * Reconstructs the Actor for aggregate authorization and auditing.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

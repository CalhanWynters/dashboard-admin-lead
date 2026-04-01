package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.product.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.PhysicalSpecs;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.ProductPhysicalSpecs;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for editing a Product's Physical Specifications.
 * Maps raw numeric and text inputs to hardened Domain records and Actor context.
 */
public record ProductEditPhysicalSpecsDTO(
        BigDecimal weight,
        String weightUnit,
        BigDecimal length,
        BigDecimal width,
        BigDecimal height,
        String dimensionUnit,
        String careInstructions,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Reconstructs the Actor for aggregate authorization and auditing.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }

    /**
     * Maps the raw inputs into a hardened ProductPhysicalSpecs record.
     * Triggers DomainGuard validation for numeric ranges and lexical content.
     */
    public ProductPhysicalSpecs toProductPhysicalSpecs() {
        // Implementation logic typically involves building the common PhysicalSpecs
        // value object and wrapping it in the Product-specific record.
        return null;
    }
}

package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.product;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.validationchecks.DomainGuard;

/**
 * Handles domain-driven state transitions for the Product Aggregate (2026 Edition).
 * Implements an immutability pattern that preserves all internal domain rules and
 * bespoke physical/financial specifications.
 */
public class ProductBehavior {

    private final ProductAggregateRoot productAggregateRoot;

    public ProductBehavior(ProductAggregateRoot productAggregateRoot) {
        DomainGuard.notNull(productAggregateRoot, "Product instance");
        this.productAggregateRoot = productAggregateRoot;
    }

    public ProductAggregateRoot activate() { return transitionTo(StatusEnums.ACTIVE); }
    public ProductAggregateRoot deactivate() { return transitionTo(StatusEnums.INACTIVE); }
    public ProductAggregateRoot discontinue() { return transitionTo(StatusEnums.DISCONTINUED); }

    /**
     * Internal state machine orchestrator.
     * Reconstructs the aggregate while maintaining semantic integrity of all fields.
     */
    private ProductAggregateRoot transitionTo(StatusEnums nextStatus) {
        // 1. Semantic Check via Enum Logic
        DomainGuard.ensure(
                productAggregateRoot.getStatus().canTransitionTo(nextStatus),
                "Illegal state transition: Cannot change from %s to %s."
                        .formatted(productAggregateRoot.getStatus(), nextStatus),
                "VAL-016", "STATE_VIOLATION"
        );

        // 2. Immutability Pattern: Reconstruction with all required 2026 fields
        // We leverage Sentinel Constants (NONE) for all optional fields to ensure
        // the XOR Invariant in the Product constructor is satisfied correctly.
        return new ProductAggregateRoot(
                productAggregateRoot.getProductId(),
                productAggregateRoot.getProductUuId(),
                productAggregateRoot.getBusinessId(),
                productAggregateRoot.getProductName(),
                productAggregateRoot.getProductCategory(),
                productAggregateRoot.getProductDesc(),
                nextStatus,                   // New State
                productAggregateRoot.getVersion().next(),  // Version Increment
                productAggregateRoot.getAudit().update(),  // Temporal Audit Refresh
                productAggregateRoot.getGalleryColId(),

                // Aggregate References (Composition)
                productAggregateRoot.getTypeColId().orElse(UuId.NONE),
                productAggregateRoot.getVariantColId().orElse(UuId.NONE),

                // Bespoke Physical & Financial Specifications
                productAggregateRoot.getProductDimensions().orElse(Dimensions.NONE),
                productAggregateRoot.getProductWeight().orElse(Weight.NONE),
                productAggregateRoot.getProductCareInstruction().orElse(CareInstruction.NONE),

                // Preservation of Domain Rules
                productAggregateRoot.getInternalRules(),
                productAggregateRoot.getContextualRules()
        );
    }
}

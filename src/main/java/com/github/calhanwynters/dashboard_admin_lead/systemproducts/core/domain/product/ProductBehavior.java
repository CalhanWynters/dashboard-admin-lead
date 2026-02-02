package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;

/**
 * Modern Behavioral Service for Product Aggregates.
 * Orchestrates state transitions while maintaining the XOR invariants and versioning.
 */
public class ProductBehavior {

    private final ProductAggregate product;

    public ProductBehavior(ProductAggregate product) {
        DomainGuard.notNull(product, "Product Aggregate instance");
        this.product = product;
    }

    public ProductAggregate activate() { return transitionTo(ProductStatus.ACTIVE); }
    public ProductAggregate deactivate() { return transitionTo(ProductStatus.INACTIVE); }
    public ProductAggregate discontinue() { return transitionTo(ProductStatus.DISCONTINUED); }

    /**
     * Executes state transitions and increments domain versioning.
     * Note: In the modern 2026 architecture, we modify the aggregate state
     * and register domain events rather than full reconstruction.
     */
    private ProductAggregate transitionTo(ProductStatus nextStatus) {
        // 1. Semantic Check via delegated Enum Logic in the wrapper
        DomainGuard.ensure(
                product.getProductStatus().canTransitionTo(nextStatus),
                "Illegal state transition: Cannot change from %s to %s."
                        .formatted(product.getProductStatus(), nextStatus),
                "VAL-016", "STATE_VIOLATION"
        );

        // 2. Apply State Changes
        // Ensure these fields are accessible for mutation within the same package
        product.updateStatus(nextStatus);
        product.incrementVersion();

        // 3. Register Domain Event (Standard Spring Data pattern)
        // product.registerEvent(new ProductStatusChangedEvent(product.getProductUuId(), nextStatus));

        return product;
    }
}

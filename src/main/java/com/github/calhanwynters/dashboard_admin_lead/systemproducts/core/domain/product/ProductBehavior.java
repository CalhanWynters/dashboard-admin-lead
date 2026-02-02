package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;

/**
 * Modern Behavioral Service for Product Aggregates.
 * Orchestrates state transitions with mandatory audit attribution.
 */
public class ProductBehavior {

    private final ProductAggregate product;

    public ProductBehavior(ProductAggregate product) {
        DomainGuard.notNull(product, "Product Aggregate instance");
        this.product = product;
    }

    // Behavior methods now demand an Actor
    public ProductAggregate activate(Actor actor) { return transitionTo(ProductStatus.ACTIVE, actor); }
    public ProductAggregate deactivate(Actor actor) { return transitionTo(ProductStatus.INACTIVE, actor); }
    public ProductAggregate discontinue(Actor actor) { return transitionTo(ProductStatus.DISCONTINUED, actor); }

    /**
     * Executes state transitions, increments versioning, and attributes the change to an Actor.
     */
    private ProductAggregate transitionTo(ProductStatus nextStatus, Actor actor) {
        // 1. Semantic Check via delegated Enum Logic
        DomainGuard.ensure(
                product.getProductStatus().canTransitionTo(nextStatus),
                "Illegal state transition: Cannot change from %s to %s."
                        .formatted(product.getProductStatus(), nextStatus),
                "VAL-016", "STATE_VIOLATION"
        );

        // 2. Apply State Changes with Audit Trail
        // These calls now trigger recordUpdate(actor) inside the aggregate
        product.updateStatus(nextStatus, actor);
        product.incrementVersion(actor);

        return product;
    }
}

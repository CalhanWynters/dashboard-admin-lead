package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;

/**
 * Modern Behavioral Service for Product Aggregates.
 * Orchestrates state transitions with mandatory audit attribution.
 */
public class ProductBehavior {

    private final ProductAggregateRoot product;

    public ProductBehavior(ProductAggregateRoot product) {
        DomainGuard.notNull(product, "Product Aggregate instance");
        this.product = product;
    }

    // Behavior methods now demand an Actor
    public ProductAggregateRoot activate(Actor actor) { return transitionTo(ProductStatus.ACTIVE, actor); }
    public ProductAggregateRoot deactivate(Actor actor) { return transitionTo(ProductStatus.INACTIVE, actor); }
    public ProductAggregateRoot discontinue(Actor actor) { return transitionTo(ProductStatus.DISCONTINUED, actor); }

    private ProductAggregateRoot transitionTo(ProductStatus nextStatus, Actor actor) {
        DomainGuard.ensure(
                product.getProductStatus().canTransitionTo(nextStatus),
                "Illegal transition from %s to %s.".formatted(product.getProductStatus(), nextStatus),
                "VAL-016", "STATE_VIOLATION"
        );

        // Atomic update: Status + Version + Audit
        product.performStatusTransition(nextStatus, actor);

        return product;
    }

}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.ProductUuId;

/**
 * Domain Event fired when a soft-deleted product is returned to an active state.
 * Required for SOC 2 lifecycle traceability.
 */
@DomainEvent(name = "Product Restored", namespace = "product")
public record ProductRestoredEvent(ProductUuId productId, Actor actor) {}

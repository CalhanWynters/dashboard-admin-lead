package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;


@DomainEvent(name = "Product Status Changed", namespace = "product")
public record ProductStatusChangedEvent(
        ProductUuId productId,
        ProductStatus oldStatus,
        ProductStatus newStatus,
        ProductVersion version,
        Actor actor
) {}
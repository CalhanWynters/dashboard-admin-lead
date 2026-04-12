package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.events;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent(name = "Product Region Updated", namespace = "products")
public record ProductRegionUpdatedEvent(
        ProductUuId id,
        ProductRegion oldRegion,
        ProductRegion newRegion,
        Actor actor
) { }

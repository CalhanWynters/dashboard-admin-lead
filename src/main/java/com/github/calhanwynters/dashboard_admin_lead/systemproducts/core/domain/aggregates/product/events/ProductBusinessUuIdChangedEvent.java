package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.events;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent(name = "Product Business ID Changed", namespace = "product")
public record ProductBusinessUuIdChangedEvent(
        ProductUuId productUuId,
        ProductBusinessUuId oldId,
        ProductBusinessUuId newId,
        Actor actor
) {}

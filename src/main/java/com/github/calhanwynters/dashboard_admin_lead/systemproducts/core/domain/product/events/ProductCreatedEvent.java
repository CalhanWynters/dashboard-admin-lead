package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;

@DomainEvent(name = "Product Created", namespace = "product")
public record ProductCreatedEvent(
        ProductUuId productId,
        ProductBusinessUuId businessId,
        ProductStatus initialStatus,
        Actor creator
) {}
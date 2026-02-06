package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;


@DomainEvent(name = "Product Discontinued", namespace = "product")
public record ProductDiscontinuedEvent(ProductUuId productId, Actor actor) {}
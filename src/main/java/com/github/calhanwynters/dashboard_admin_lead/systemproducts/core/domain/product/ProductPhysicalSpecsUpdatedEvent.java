package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;


@DomainEvent(name = "Product Physical Specs Updated", namespace = "product")
public record ProductPhysicalSpecsUpdatedEvent(ProductUuId productId, ProductPhysicalSpecs specs, Actor actor) {}
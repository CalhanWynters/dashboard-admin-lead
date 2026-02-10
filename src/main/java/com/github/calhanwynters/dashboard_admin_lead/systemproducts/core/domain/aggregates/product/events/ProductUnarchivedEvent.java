package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;

@DomainEvent(name = "Product Unarchived", namespace = "product")
public record ProductUnarchivedEvent(ProductUuId productId, Actor actor) {}

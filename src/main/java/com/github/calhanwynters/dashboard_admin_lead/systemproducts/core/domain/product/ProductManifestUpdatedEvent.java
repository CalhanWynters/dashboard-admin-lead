package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;

@DomainEvent(name = "Product Manifest Updated", namespace = "product")
public record ProductManifestUpdatedEvent(ProductUuId productId, ProductManifest newManifest, Actor actor) {}

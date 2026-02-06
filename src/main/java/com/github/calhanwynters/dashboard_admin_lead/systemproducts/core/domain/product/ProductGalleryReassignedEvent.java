package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import org.jmolecules.event.annotation.DomainEvent;
// Explicitly import the types from the wrappers
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.ProductUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.GalleryUuId;

@DomainEvent(name = "Product Gallery Reassigned", namespace = "product")
public record ProductGalleryReassignedEvent(ProductUuId productId, GalleryUuId newGalleryId, Actor actor) {}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.events;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import org.jmolecules.event.annotation.DomainEvent;
// Explicitly import the types from the wrappers
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.ProductUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

@DomainEvent(name = "Product Gallery Reassigned", namespace = "product")
public record ProductGalleryReassignedEvent(ProductUuId productId, GalleryUuId newGalleryId, Actor actor) {}

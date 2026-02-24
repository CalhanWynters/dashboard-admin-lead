package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryBusinessUuId;

@DomainEvent(name = "Gallery Data Synced", namespace = "gallery")
public record GalleryDataSyncedEvent(
        GalleryUuId galleryUuId,
        GalleryBusinessUuId galleryBusinessUuId,
        boolean isPublic,
        ProductBooleans productBooleans,
        Actor actor
) { }

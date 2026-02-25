package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.events;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.*;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent(name = "Gallery Business ID Changed", namespace = "gallery")
public record GalleryBusinessUuIdChangedEvent(
        GalleryUuId galleryUuId,
        GalleryBusinessUuId oldId,
        GalleryBusinessUuId newId,
        Actor actor
) {}

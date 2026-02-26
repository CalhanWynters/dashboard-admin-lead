package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

/**
 * Domain Event fired when a gallery's public visibility status is toggled.
 */
@DomainEvent(name = "Gallery Public Status Toggled", namespace = "gallery")
public record GalleryPublicStatusToggledEvent(
        GalleryUuId galleryUuId,
        boolean isPublic,
        Actor actor
) {}

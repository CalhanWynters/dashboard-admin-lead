package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.ImageUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.*;

@DomainEvent(name = "Gallery Soft Deleted", namespace = "gallery")
public record GallerySoftDeletedEvent(GalleryUuId galleryUuId, Actor actor) {}

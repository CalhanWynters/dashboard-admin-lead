package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.ImageUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.*;

@DomainEvent(name = "Image Added to Gallery", namespace = "gallery")
public record ImageAddedToGalleryEvent(GalleryUuId galleryUuId, ImageUuId imageUuId, Actor actor) {}

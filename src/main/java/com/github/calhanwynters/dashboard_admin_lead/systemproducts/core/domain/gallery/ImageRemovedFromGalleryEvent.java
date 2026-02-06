package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.*;
import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.*;


@DomainEvent(name = "Image Removed From Gallery", namespace = "gallery")
public record ImageRemovedFromGalleryEvent(GalleryUuId galleryUuId, ImageUuId imageUuId, Actor actor) {}
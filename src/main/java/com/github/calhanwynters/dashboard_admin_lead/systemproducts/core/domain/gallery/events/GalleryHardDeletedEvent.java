package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.*;

@DomainEvent(name = "Gallery Hard Deleted", namespace = "gallery")
public record GalleryHardDeletedEvent(GalleryUuId galleryUuId, Actor actor) {}
package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.*;

@DomainEvent(name = "Gallery Reordered", namespace = "gallery")
public record GalleryReorderedEvent(GalleryUuId galleryUuId, Actor actor) {}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.*;

@DomainEvent(name = "Gallery Publicity Changed", namespace = "gallery")
public record GalleryPublicityChangedEvent(GalleryUuId galleryUuId, boolean isPublic, Actor actor) {}
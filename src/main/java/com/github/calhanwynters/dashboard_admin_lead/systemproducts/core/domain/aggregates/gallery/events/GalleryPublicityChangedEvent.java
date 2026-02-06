package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.*;

@DomainEvent(name = "Gallery Publicity Changed", namespace = "gallery")
public record GalleryPublicityChangedEvent(GalleryUuId galleryUuId, boolean isPublic, Actor actor) {}
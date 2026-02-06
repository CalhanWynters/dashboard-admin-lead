package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.*;

@DomainEvent(name = "Gallery Restored", namespace = "gallery")
public record GalleryRestoredEvent(GalleryUuId galleryUuId, Actor actor) {}
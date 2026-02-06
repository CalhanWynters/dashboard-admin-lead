package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.*;

@DomainEvent(name = "Gallery Limit Reached", namespace = "gallery")
public record GalleryLimitReachedEvent(GalleryUuId galleryUuId, int currentCount, Actor actor) {}
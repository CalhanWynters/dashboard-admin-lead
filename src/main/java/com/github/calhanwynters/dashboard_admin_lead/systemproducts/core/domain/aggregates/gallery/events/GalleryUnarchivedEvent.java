package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

/**
 * Domain Event fired when a gallery is removed from the archive.
 */
@DomainEvent(name = "Gallery Unarchived", namespace = "gallery")
public record GalleryUnarchivedEvent(GalleryUuId galleryUuId, Actor actor) {}

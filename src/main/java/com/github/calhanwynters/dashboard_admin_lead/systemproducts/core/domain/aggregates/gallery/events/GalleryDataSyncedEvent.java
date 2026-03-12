package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryBusinessUuId;

/**
 * Event published when an admin manual triggers a Kafka sync for a Gallery.
 * Refactored for 2026 Edition: Uses LifecycleState record.
 */
@DomainEvent(name = "Gallery Data Synced", namespace = "gallery")
public record GalleryDataSyncedEvent(
        GalleryUuId galleryUuId,
        GalleryBusinessUuId galleryBusinessUuId,
        boolean isPublic,
        LifecycleState lifecycleState, // Updated from ProductBooleansLEGACY
        Actor actor
) { }

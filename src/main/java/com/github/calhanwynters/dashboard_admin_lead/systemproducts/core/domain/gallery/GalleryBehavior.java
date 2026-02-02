package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

public class GalleryBehavior {

    private final GalleryAggregate gallery;

    public GalleryBehavior(GalleryAggregate gallery) {
        DomainGuard.notNull(gallery, "Gallery Aggregate instance");
        this.gallery = gallery;
    }

    /**
     * Use this when the gallery state is modified (e.g., reordering images).
     */
    public void markAsUpdated(Actor actor) {
        // Triggers the BaseAggregateRoot audit refresh
        this.gallery.recordUpdate(actor);
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

public class ImageBehavior {

    private final ImageAggregate image;

    public ImageBehavior(ImageAggregate image) {
        DomainGuard.notNull(image, "Image Aggregate instance");
        this.image = image;
    }

    public void updateMetadata(Actor actor) {
        // Business logic for metadata updates would go here
        this.image.recordUpdate(actor);
    }
}

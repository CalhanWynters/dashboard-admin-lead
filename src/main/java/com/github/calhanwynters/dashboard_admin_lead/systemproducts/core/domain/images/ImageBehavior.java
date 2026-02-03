package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.ImageName;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.ImageDescription;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

public class ImageBehavior {

    private final ImageAggregate image;

    public ImageBehavior(ImageAggregate image) {
        DomainGuard.notNull(image, "Image Aggregate instance");
        this.image = image;
    }

    public void updateMetadata(ImageName name, ImageDescription description, Actor actor) {
        DomainGuard.notNull(actor, "Actor");
        this.image.updateMetadata(name, description, actor);
    }

}

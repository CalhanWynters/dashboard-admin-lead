package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.GalleryId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.GalleryUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.GalleryBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import org.springframework.data.domain.AbstractAggregateRoot;

public class GalleryAggregate extends AbstractAggregateRoot<GalleryAggregate> {

    private final GalleryId galleryId;
    private final GalleryUuId galleryUuId;
    private final GalleryBusinessUuId galleryBusinessUuId;

    // Constructor
    public GalleryAggregate(GalleryId galleryId,
                            GalleryUuId galleryUuId,
                            GalleryBusinessUuId galleryBusinessUuId) {
        // Validation checks
        DomainGuard.notNull(galleryId, "Gallery PK ID");
        DomainGuard.notNull(galleryUuId, "Gallery UUID");
        DomainGuard.notNull(galleryBusinessUuId, "Gallery Business UUID");

        this.galleryId = galleryId;
        this.galleryUuId = galleryUuId;
        this.galleryBusinessUuId = galleryBusinessUuId;
    }

    // Getters
    public GalleryId getGalleryId() {
        return galleryId;
    }

    public GalleryUuId getGalleryUuId() {
        return galleryUuId;
    }

    public GalleryBusinessUuId getGalleryBusinessUuId() {
        return galleryBusinessUuId;
    }
}

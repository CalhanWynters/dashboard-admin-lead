package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.*;

/**
 * Aggregate Root for Gallery management.
 * Encapsulates update attribution within the domain model.
 */
public class GalleryAggregate extends BaseAggregateRoot<GalleryAggregate> {

    private final GalleryId galleryId;
    private final GalleryUuId galleryUuId;
    private final GalleryBusinessUuId galleryBusinessUuId;

    public GalleryAggregate(GalleryId galleryId,
                            GalleryUuId galleryUuId,
                            GalleryBusinessUuId galleryBusinessUuId,
                            AuditMetadata auditMetadata) {
        super(auditMetadata);
        // Identity validation
        this.galleryId = DomainGuard.notNull(galleryId, "Gallery PK ID");
        this.galleryUuId = DomainGuard.notNull(galleryUuId, "Gallery UUID");
        this.galleryBusinessUuId = DomainGuard.notNull(galleryBusinessUuId, "Gallery Business UUID");
    }

    public void markAsUpdated(Actor actor) {
        DomainGuard.notNull(actor, "Actor");
        this.recordUpdate(actor);
        this.registerEvent(new GalleryTouchedEvent(this.galleryUuId, actor));
    }

    public void reorderImages(Actor actor) {
        DomainGuard.notNull(actor, "Actor");
        this.recordUpdate(actor);
        this.registerEvent(new GalleryReorderedEvent(this.galleryUuId, actor));
    }

    public void addImage(com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.ImageUuId imageUuId, Actor actor) {
        DomainGuard.notNull(imageUuId, "Image UUID");
        this.recordUpdate(actor);
        this.registerEvent(new ImageAddedToGalleryEvent(this.galleryUuId, imageUuId, actor));
    }

    public void softDelete(Actor actor) {
        this.recordUpdate(actor);
        this.registerEvent(new GallerySoftDeletedEvent(this.galleryUuId, actor));
    }

    public void hardDelete(Actor actor) {
        this.registerEvent(new GalleryHardDeletedEvent(this.galleryUuId, actor));
    }

    // Getters
    public GalleryId getGalleryId() { return galleryId; }
    public GalleryUuId getGalleryUuId() { return galleryUuId; }
    public GalleryBusinessUuId getGalleryBusinessUuId() { return galleryBusinessUuId; }
}

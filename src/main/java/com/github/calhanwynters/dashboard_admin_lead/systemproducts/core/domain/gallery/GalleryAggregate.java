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

        DomainGuard.notNull(galleryId, "Gallery PK ID");
        DomainGuard.notNull(galleryUuId, "Gallery UUID");
        DomainGuard.notNull(galleryBusinessUuId, "Gallery Business UUID");

        this.galleryId = galleryId;
        this.galleryUuId = galleryUuId;
        this.galleryBusinessUuId = galleryBusinessUuId;
    }

    /**
     * Updates the audit metadata when the gallery state is modified.
     * Essential for tracking changes like image reordering or metadata updates.
     */
    public void markAsUpdated(Actor actor) {
        DomainGuard.notNull(actor, "Actor performing the update");
        this.recordUpdate(actor);
    }

    // Getters
    public GalleryId getGalleryId() { return galleryId; }
    public GalleryUuId getGalleryUuId() { return galleryUuId; }
    public GalleryBusinessUuId getGalleryBusinessUuId() { return galleryBusinessUuId; }
}

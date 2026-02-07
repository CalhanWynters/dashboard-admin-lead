package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.events.*;

import java.util.ArrayList;
import java.util.List;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

/**
 * Aggregate Root for Gallery management.
 */
public class GalleryAggregate extends BaseAggregateRoot<GalleryAggregate> {

    private final GalleryId galleryId;
    private final GalleryUuId galleryUuId;
    private final GalleryBusinessUuId galleryBusinessUuId;

    // New state fields to support Behavior checks
    private final List<ImageUuId> imageUuIds = new ArrayList<>();
    private boolean isPublic;

    public GalleryAggregate(GalleryId galleryId,
                            GalleryUuId galleryUuId,
                            GalleryBusinessUuId galleryBusinessUuId,
                            boolean isPublic,
                            List<ImageUuId> imageUuIds,
                            AuditMetadata auditMetadata) {
        super(auditMetadata);
        this.galleryId = DomainGuard.notNull(galleryId, "Gallery PK ID");
        this.galleryUuId = DomainGuard.notNull(galleryUuId, "Gallery UUID");
        this.galleryBusinessUuId = DomainGuard.notNull(galleryBusinessUuId, "Gallery Business UUID");
        this.isPublic = isPublic;
        if (imageUuIds != null) {
            this.imageUuIds.addAll(imageUuIds);
        }
    }

    /**
     * Static Factory for new Gallery creation.
     */
    public static GalleryAggregate create(GalleryUuId uuId, GalleryBusinessUuId bUuId, Actor actor) {
        GalleryBehavior.verifyCreationAuthority(actor);

        GalleryAggregate aggregate = new GalleryAggregate(null, uuId, bUuId, false, List.of(), AuditMetadata.create(actor));
        aggregate.registerEvent(new GalleryCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    public void addImage(ImageUuId imageUuId, Actor actor) {
        var validatedImage = GalleryBehavior.evaluateImageAddition(imageUuId, this.imageUuIds.size(), actor);

        this.applyChange(actor,
                new ImageAddedToGalleryEvent(galleryUuId, validatedImage, actor),
                () -> this.imageUuIds.add(validatedImage));
    }

    public void removeImage(ImageUuId imageUuId, Actor actor) {
        var validatedImage = GalleryBehavior.evaluateImageRemoval(imageUuId, this.imageUuIds.contains(imageUuId), actor);

        this.applyChange(actor,
                new ImageRemovedFromGalleryEvent(galleryUuId, validatedImage, actor),
                () -> this.imageUuIds.remove(validatedImage));
    }

    public void reorderImages(Actor actor) {
        GalleryBehavior.verifyReorderable(this.imageUuIds.size(), actor);

        this.applyChange(actor, new GalleryReorderedEvent(galleryUuId, actor), null);
    }

    public void changePublicity(boolean isPublic, Actor actor) {
        var nextStatus = GalleryBehavior.evaluatePublicityChange(this.isPublic, isPublic, actor);

        this.applyChange(actor,
                new GalleryPublicityChangedEvent(galleryUuId, nextStatus, actor),
                () -> this.isPublic = nextStatus);
    }

    public void softDelete(Actor actor) {
        GalleryBehavior.verifyDeletable(actor);
        this.applyChange(actor, new GallerySoftDeletedEvent(galleryUuId, actor), null);
    }

    public void restore(Actor actor) {
        GalleryBehavior.verifyRestorable(actor);
        this.applyChange(actor, new GalleryRestoredEvent(galleryUuId, actor), null);
    }

    public void hardDelete(Actor actor) {
        GalleryBehavior.verifyHardDeleteAuthority(actor);
        this.applyChange(actor, new GalleryHardDeletedEvent(galleryUuId, actor), null);
    }

    public void markAsUpdated(Actor actor) {
        // No specific role required for a "touch" but we audit the actor anyway
        this.applyChange(actor, new GalleryTouchedEvent(galleryUuId, actor), null);
    }


    // --- GETTERS ---
    public GalleryId getGalleryId() { return galleryId; }
    public GalleryUuId getGalleryUuId() { return galleryUuId; }
    public GalleryBusinessUuId getGalleryBusinessUuId() { return galleryBusinessUuId; }
    public List<ImageUuId> getImageUuIds() { return List.copyOf(imageUuIds); }
    public boolean isPublic() { return isPublic; }
}

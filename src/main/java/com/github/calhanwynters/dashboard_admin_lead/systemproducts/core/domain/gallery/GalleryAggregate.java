package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.events.*;

import java.util.ArrayList;
import java.util.List;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.ImageUuId;

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
        // New galleries start private with 0 images
        GalleryAggregate aggregate = new GalleryAggregate(null, uuId, bUuId, false, List.of(), AuditMetadata.create(actor));
        aggregate.registerEvent(new GalleryCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS (Two-Liner Pattern) ---

    public void addImage(ImageUuId imageUuId, Actor actor) {
        // Line 1: Pure Calculation (Now passing current size)
        var validatedImage = GalleryBehavior.evaluateImageAddition(imageUuId, this.imageUuIds.size());

        // Line 2: Execution
        this.applyChange(actor, new ImageAddedToGalleryEvent(galleryUuId, validatedImage, actor), () -> this.imageUuIds.add(validatedImage));
    }

    public void removeImage(ImageUuId imageUuId, Actor actor) {
        // Line 1: Pure Calculation
        var validatedImage = GalleryBehavior.evaluateImageRemoval(imageUuId, this.imageUuIds.contains(imageUuId));

        // Line 2: Execution
        this.applyChange(actor, new ImageRemovedFromGalleryEvent(galleryUuId, validatedImage, actor), () -> this.imageUuIds.remove(validatedImage));
    }

    public void reorderImages(Actor actor) {
        // Line 1: Pure Calculation (Now passing current size)
        GalleryBehavior.verifyReorderable(this.imageUuIds.size());

        // Line 2: Execution
        this.applyChange(actor, new GalleryReorderedEvent(galleryUuId, actor), null);
    }

    public void changePublicity(boolean isPublic, Actor actor) {
        // Line 1: Pure Logic (Now resolves 'this.isPublic')
        var nextStatus = GalleryBehavior.evaluatePublicityChange(this.isPublic, isPublic);

        // Line 2: Execution
        this.applyChange(actor, new GalleryPublicityChangedEvent(galleryUuId, nextStatus, actor), () -> this.isPublic = nextStatus);
    }

    public void softDelete(Actor actor) {
        GalleryBehavior.verifyDeletable();

        this.applyChange(actor, new GallerySoftDeletedEvent(galleryUuId, actor), () -> {
            // logic for soft delete state if needed
        });
    }

    public void restore(Actor actor) {
        GalleryBehavior.verifyRestorable();

        this.applyChange(actor, new GalleryRestoredEvent(galleryUuId, actor), () -> {
            // logic for restoration state if needed
        });
    }

    public void hardDelete(Actor actor) {
        this.applyChange(actor, new GalleryHardDeletedEvent(galleryUuId, actor), null);
    }

    public void markAsUpdated(Actor actor) {
        this.applyChange(actor, new GalleryTouchedEvent(galleryUuId, actor), null);
    }

    // --- GETTERS ---
    public GalleryId getGalleryId() { return galleryId; }
    public GalleryUuId getGalleryUuId() { return galleryUuId; }
    public GalleryBusinessUuId getGalleryBusinessUuId() { return galleryBusinessUuId; }
    public List<ImageUuId> getImageUuIds() { return List.copyOf(imageUuIds); }
    public boolean isPublic() { return isPublic; }
}

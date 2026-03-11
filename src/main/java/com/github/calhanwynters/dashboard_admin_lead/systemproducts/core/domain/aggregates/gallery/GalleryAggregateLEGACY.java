package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.LEGACYBaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleansLEGACY;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.events.*;

import java.util.ArrayList;
import java.util.List;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

public class GalleryAggregateLEGACY extends LEGACYBaseAggregateRoot<GalleryAggregateLEGACY> {

    private final GalleryId galleryId;
    private final GalleryUuId galleryUuId;
    private GalleryBusinessUuId galleryBusinessUuId;

    private final List<ImageUuId> imageUuIds = new ArrayList<>();
    private boolean isPublic;
    private ProductBooleansLEGACY productBooleansLEGACY; // Record integration
    // Add Version-Based Optimistic Locking "optLockVer"
    // Add Schema-Based Versioning "schemaVer"


    public GalleryAggregateLEGACY(GalleryId galleryId,
                                  GalleryUuId galleryUuId,
                                  GalleryBusinessUuId galleryBusinessUuId,
                                  boolean isPublic,
                                  List<ImageUuId> imageUuIds,
                                  ProductBooleansLEGACY productBooleansLEGACY, // Added param
                                  AuditMetadata auditMetadata) {
        super(auditMetadata);
        this.galleryId = DomainGuard.notNull(galleryId, "Gallery PK ID");
        this.galleryUuId = DomainGuard.notNull(galleryUuId, "Gallery UUID");
        this.galleryBusinessUuId = DomainGuard.notNull(galleryBusinessUuId, "Gallery Business UUID");
        this.isPublic = isPublic;
        if (imageUuIds != null) {
            this.imageUuIds.addAll(imageUuIds);
        }
        this.productBooleansLEGACY = (productBooleansLEGACY != null) ? productBooleansLEGACY : new ProductBooleansLEGACY(false, false);
    }

    public static GalleryAggregateLEGACY create(GalleryUuId uuId, GalleryBusinessUuId bUuId, Actor actor) {
        GalleryBehavior.verifyCreationAuthority(actor);

        GalleryAggregateLEGACY aggregate = new GalleryAggregateLEGACY(
                null, uuId, bUuId, false, List.of(), new ProductBooleansLEGACY(false, false), AuditMetadata.create(actor)
        );
        aggregate.registerEvent(new GalleryCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void updateBusinessUuId(GalleryBusinessUuId newId, Actor actor) {
        GalleryBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());

        // Validate using your existing logic (Admin-only, non-null, difference check)
        var validatedId = GalleryBehavior.evaluateBusinessIdChange(this.galleryBusinessUuId, newId, actor);

        this.applyChange(actor,
                new GalleryBusinessUuIdChangedEvent(galleryUuId, this.galleryBusinessUuId, validatedId, actor),
                () -> this.galleryBusinessUuId = validatedId);
    }

    public void togglePublicStatus(boolean newStatus, Actor actor) {
        GalleryBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());

        // Call the behavior to validate roles and state redundancy
        var validatedStatus = GalleryBehavior.evaluatePublicityChange(this.isPublic, newStatus, actor);

        this.applyChange(actor,
                new GalleryPublicStatusToggledEvent(galleryUuId, validatedStatus, actor),
                () -> this.isPublic = validatedStatus
        );
    }

    public void syncToKafka(Actor actor) {
        GalleryBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        GalleryBehavior.verifySyncAuthority(actor);

        this.applyChange(actor,
                new GalleryDataSyncedEvent(galleryUuId, galleryBusinessUuId, isPublic, productBooleansLEGACY, actor),
                null);
    }

    public void addImage(ImageUuId imageUuId, Actor actor) {
        GalleryBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        var validatedImage = GalleryBehavior.evaluateImageAddition(imageUuId, this.imageUuIds.size(), actor);

        this.applyChange(actor,
                new ImageAddedToGalleryEvent(galleryUuId, validatedImage, actor),
                () -> this.imageUuIds.add(validatedImage));
    }

    public void removeImage(ImageUuId imageUuId, Actor actor) {
        GalleryBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        var validatedImage = GalleryBehavior.evaluateImageRemoval(imageUuId, this.imageUuIds.contains(imageUuId), actor);

        this.applyChange(actor,
                new ImageRemovedFromGalleryEvent(galleryUuId, validatedImage, actor),
                () -> this.imageUuIds.remove(validatedImage));
    }

    public void archive(Actor actor) {
        GalleryBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new GalleryArchivedEvent(galleryUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(true, this.productBooleansLEGACY.softDeleted())
        );
    }

    public void unarchive(Actor actor) {
        GalleryBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new GalleryUnarchivedEvent(galleryUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(false, this.productBooleansLEGACY.softDeleted())
        );
    }

    public void softDelete(Actor actor) {
        GalleryBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        GalleryBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new GallerySoftDeletedEvent(galleryUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(this.productBooleansLEGACY.archived(), true)
        );
    }

    public void restore(Actor actor) {
        if (!this.productBooleansLEGACY.softDeleted()) return;
        GalleryBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new GalleryRestoredEvent(galleryUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(this.productBooleansLEGACY.archived(), false)
        );
    }

    public void hardDelete(Actor actor) {
        GalleryBehavior.verifyHardDeleteAuthority(actor);
        this.applyChange(actor, new GalleryHardDeletedEvent(galleryUuId, actor), null);
    }

    // --- GETTERS ---
    public ProductBooleansLEGACY getProductBooleans() { return productBooleansLEGACY; }
    public boolean isDeleted() { return productBooleansLEGACY.softDeleted(); }
    public boolean isArchived() { return productBooleansLEGACY.archived(); }
    public GalleryId getGalleryId() { return galleryId; }
    public GalleryUuId getGalleryUuId() { return galleryUuId; }
    public GalleryBusinessUuId getGalleryBusinessUuId() { return galleryBusinessUuId; }
    public List<ImageUuId> getImageUuIds() { return List.copyOf(imageUuIds); }
    public boolean isPublic() { return isPublic; }
}

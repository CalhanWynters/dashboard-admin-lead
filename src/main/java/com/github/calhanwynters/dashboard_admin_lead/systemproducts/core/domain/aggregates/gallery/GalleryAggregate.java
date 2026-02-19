package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.events.*;

import java.util.ArrayList;
import java.util.List;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

public class GalleryAggregate extends BaseAggregateRoot<GalleryAggregate> {

    private final GalleryId galleryId;
    private final GalleryUuId galleryUuId;
    private final GalleryBusinessUuId galleryBusinessUuId;

    private final List<ImageUuId> imageUuIds = new ArrayList<>();
    private boolean isPublic;
    private ProductBooleans productBooleans; // Record integration

    public GalleryAggregate(GalleryId galleryId,
                            GalleryUuId galleryUuId,
                            GalleryBusinessUuId galleryBusinessUuId,
                            boolean isPublic,
                            List<ImageUuId> imageUuIds,
                            ProductBooleans productBooleans, // Added param
                            AuditMetadata auditMetadata) {
        super(auditMetadata);
        this.galleryId = DomainGuard.notNull(galleryId, "Gallery PK ID");
        this.galleryUuId = DomainGuard.notNull(galleryUuId, "Gallery UUID");
        this.galleryBusinessUuId = DomainGuard.notNull(galleryBusinessUuId, "Gallery Business UUID");
        this.isPublic = isPublic;
        if (imageUuIds != null) {
            this.imageUuIds.addAll(imageUuIds);
        }
        this.productBooleans = (productBooleans != null) ? productBooleans : new ProductBooleans(false, false);
    }

    public static GalleryAggregate create(GalleryUuId uuId, GalleryBusinessUuId bUuId, Actor actor) {
        GalleryBehavior.verifyCreationAuthority(actor);

        GalleryAggregate aggregate = new GalleryAggregate(
                null, uuId, bUuId, false, List.of(), new ProductBooleans(false, false), AuditMetadata.create(actor)
        );
        aggregate.registerEvent(new GalleryCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    // Need a 2-liner pattern method for GalleryUpdateBusUuIdCommand
    // Need a 2-liner pattern method for GalleryPublicizeCommand
    // Need a 2-liner pattern method for GalleryTrunkDataOut

    public void addImage(ImageUuId imageUuId, Actor actor) {
        GalleryBehavior.ensureActive(this.productBooleans.softDeleted());
        var validatedImage = GalleryBehavior.evaluateImageAddition(imageUuId, this.imageUuIds.size(), actor);

        this.applyChange(actor,
                new ImageAddedToGalleryEvent(galleryUuId, validatedImage, actor),
                () -> this.imageUuIds.add(validatedImage));
    }

    public void removeImage(ImageUuId imageUuId, Actor actor) {
        GalleryBehavior.ensureActive(this.productBooleans.softDeleted());
        var validatedImage = GalleryBehavior.evaluateImageRemoval(imageUuId, this.imageUuIds.contains(imageUuId), actor);

        this.applyChange(actor,
                new ImageRemovedFromGalleryEvent(galleryUuId, validatedImage, actor),
                () -> this.imageUuIds.remove(validatedImage));
    }

    public void archive(Actor actor) {
        GalleryBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new GalleryArchivedEvent(galleryUuId, actor),
                () -> this.productBooleans = new ProductBooleans(true, this.productBooleans.softDeleted())
        );
    }

    public void unarchive(Actor actor) {
        GalleryBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new GalleryUnarchivedEvent(galleryUuId, actor),
                () -> this.productBooleans = new ProductBooleans(false, this.productBooleans.softDeleted())
        );
    }

    public void softDelete(Actor actor) {
        GalleryBehavior.ensureActive(this.productBooleans.softDeleted());
        GalleryBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new GallerySoftDeletedEvent(galleryUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), true)
        );
    }

    public void restore(Actor actor) {
        if (!this.productBooleans.softDeleted()) return;
        GalleryBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new GalleryRestoredEvent(galleryUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), false)
        );
    }

    public void hardDelete(Actor actor) {
        GalleryBehavior.verifyHardDeleteAuthority(actor);
        this.applyChange(actor, new GalleryHardDeletedEvent(galleryUuId, actor), null);
    }

    // --- GETTERS ---
    public ProductBooleans getProductBooleans() { return productBooleans; }
    public boolean isDeleted() { return productBooleans.softDeleted(); }
    public boolean isArchived() { return productBooleans.archived(); }
    public GalleryId getGalleryId() { return galleryId; }
    public GalleryUuId getGalleryUuId() { return galleryUuId; }
    public GalleryBusinessUuId getGalleryBusinessUuId() { return galleryBusinessUuId; }
    public List<ImageUuId> getImageUuIds() { return List.copyOf(imageUuIds); }
    public boolean isPublic() { return isPublic; }
}

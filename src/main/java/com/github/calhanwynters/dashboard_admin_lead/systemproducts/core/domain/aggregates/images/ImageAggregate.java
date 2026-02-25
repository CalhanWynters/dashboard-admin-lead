package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.events.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

public class ImageAggregate extends BaseAggregateRoot<ImageAggregate> {

    private final ImageId imageId;
    private final ImageUuId imagesUuId;
    private ImagesBusinessUuId imagesBusinessUuId;
    private ImageUrl imageUrl;
    private ProductBooleans productBooleans; // Replaced boolean isArchived

    private ImageName imageName;
    private ImageDescription imageDescription;

    public ImageAggregate(ImageId imageId,
                          ImageUuId imagesUuId,
                          ImagesBusinessUuId imagesBusinessUuId,
                          ImageName imageName,
                          ImageDescription imageDescription,
                          ImageUrl imageUrl,
                          ProductBooleans productBooleans, // Updated param
                          AuditMetadata auditMetadata) {
        super(auditMetadata);
        this.imageId = DomainGuard.notNull(imageId, "Image PK ID");
        this.imagesUuId = DomainGuard.notNull(imagesUuId, "Image UUID");
        this.imagesBusinessUuId = DomainGuard.notNull(imagesBusinessUuId, "Image Business UUID");
        this.imageName = DomainGuard.notNull(imageName, "Image Name");
        this.imageDescription = DomainGuard.notNull(imageDescription, "Image Description");
        this.imageUrl = DomainGuard.notNull(imageUrl, "Image URL");
        this.productBooleans = productBooleans != null ? productBooleans : new ProductBooleans(false, false);
    }

    public static ImageAggregate create(ImageUuId uuId, ImagesBusinessUuId bUuId, ImageName name,
                                        ImageDescription desc, ImageUrl url, Actor actor) {
        ImagesBehavior.verifyCreationAuthority(actor);

        ImageAggregate aggregate = new ImageAggregate(
                null, uuId, bUuId, name, desc, url, new ProductBooleans(false, false), AuditMetadata.create(actor)
        );
        aggregate.registerEvent(new ImageUploadedEvent(uuId, url, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void updateBusinessUuId(ImagesBusinessUuId newId, Actor actor) {
        ImagesBehavior.ensureActive(this.productBooleans.softDeleted());

        // Validate using your existing logic (Admin-only, non-null, difference check)
        var validatedId = ImagesBehavior.evaluateBusinessIdChange(this.imagesBusinessUuId, newId, actor);

        this.applyChange(actor,
                new ImageBusinessUuIdChangedEvent(imagesUuId, this.imagesBusinessUuId, validatedId, actor),
                () -> this.imagesBusinessUuId = validatedId);
    }

    // Need a 2-liner pattern method for ImagesUpdateNameCommand; need to separate from updateMetadata
    // Need a 2-liner pattern method for ImagesEditDescriptionCommand; need to separate from updateMetadata
    // Delete updateMetadata
    // Need a 2-liner pattern method for ImagesEditURLCommand
    // Need a 2-liner pattern method for ImagesHardDeleteCommand

    public void syncToKafka(Actor actor) {
        ImagesBehavior.ensureActive(this.productBooleans.softDeleted());
        ImagesBehavior.verifySyncAuthority(actor);
        this.applyChange(actor, new ImageDataSyncedEvent(imagesUuId, imagesBusinessUuId, imageName, imageDescription, imageUrl, productBooleans, actor), null);
    }

    public void updateMetadata(ImageName name, ImageDescription description, Actor actor) {
        ImagesBehavior.ensureActive(this.productBooleans.softDeleted());
        var patch = ImagesBehavior.evaluateMetadataUpdate(name, description, actor);

        this.applyChange(actor,
                new ImageMetadataUpdatedEvent(this.imagesUuId, patch.name(), actor),
                () -> {
                    this.imageName = patch.name();
                    this.imageDescription = patch.description();
                }
        );
    }

    public void archive(Actor actor) {
        // Line 1: Auth & Logic
        ImagesBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record instance)
        this.applyChange(actor,
                new ImageArchivedEvent(imagesUuId, actor),
                () -> this.productBooleans = new ProductBooleans(true, this.productBooleans.softDeleted())
        );
    }

    public void unarchive(Actor actor) {
        ImagesBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new ImageUnarchivedEvent(imagesUuId, actor),
                () -> this.productBooleans = new ProductBooleans(false, this.productBooleans.softDeleted())
        );
    }

    public void softDelete(Actor actor) {
        ImagesBehavior.ensureActive(this.productBooleans.softDeleted());
        ImagesBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new ImageSoftDeletedEvent(this.imagesUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), true)
        );
    }

    public void restore(Actor actor) {
        if (!this.productBooleans.softDeleted()) return;
        ImagesBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new ImageRestoredEvent(this.imagesUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), false)
        );
    }


    // Getters
    public boolean isDeleted() { return productBooleans.softDeleted(); }
    public boolean isArchived() { return productBooleans.archived(); }
    public ProductBooleans getProductBooleans() { return productBooleans; }
    public ImageId getImageId() { return imageId; }
    public ImageUuId getImagesUuId() { return imagesUuId; }
    public ImagesBusinessUuId getImageBusinessUuId() { return imagesBusinessUuId; }
    public ImageName getImageName() { return imageName; }
    public ImageDescription getImageDescription() { return imageDescription; }
    public ImageUrl getImageUrl() { return imageUrl; }
}

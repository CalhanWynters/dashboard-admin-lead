package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.ImageUrl;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.events.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

public class ImageAggregate extends BaseAggregateRoot<ImageAggregate> {

    private final ImageId imageId;
    private final ImageUuId imageUuId;
    private final ImageBusinessUuId imageBusinessUuId;
    private final ImageUrl imageUrl;
    private ProductBooleans productBooleans; // Replaced boolean isArchived

    private ImageName imageName;
    private ImageDescription imageDescription;

    public ImageAggregate(ImageId imageId,
                          ImageUuId imageUuId,
                          ImageBusinessUuId imageBusinessUuId,
                          ImageName imageName,
                          ImageDescription imageDescription,
                          ImageUrl imageUrl,
                          ProductBooleans productBooleans, // Updated param
                          AuditMetadata auditMetadata) {
        super(auditMetadata);
        this.imageId = DomainGuard.notNull(imageId, "Image PK ID");
        this.imageUuId = DomainGuard.notNull(imageUuId, "Image UUID");
        this.imageBusinessUuId = DomainGuard.notNull(imageBusinessUuId, "Image Business UUID");
        this.imageName = DomainGuard.notNull(imageName, "Image Name");
        this.imageDescription = DomainGuard.notNull(imageDescription, "Image Description");
        this.imageUrl = DomainGuard.notNull(imageUrl, "Image URL");
        this.productBooleans = productBooleans != null ? productBooleans : new ProductBooleans(false, false);
    }

    public static ImageAggregate create(ImageUuId uuId, ImageBusinessUuId bUuId, ImageName name,
                                        ImageDescription desc, ImageUrl url, Actor actor) {
        ImagesBehavior.verifyCreationAuthority(actor);

        ImageAggregate aggregate = new ImageAggregate(
                null, uuId, bUuId, name, desc, url, new ProductBooleans(false, false), AuditMetadata.create(actor)
        );
        aggregate.registerEvent(new ImageUploadedEvent(uuId, url, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    // Need a 2-liner pattern method for ImagesUpdateBusUuIdCommand
    // Need a 2-liner pattern method for ImagesUpdateNameCommand; need to separate from updateMetadata
    // Need a 2-liner pattern method for ImagesEditDescriptionCommand; need to separate from updateMetadata
    // Delete updateMetadata
    // Need a 2-liner pattern method for ImagesEditURLCommand
    // Need a 2-liner pattern method for ImagesHardDeleteCommand
    // Need a 2-liner pattern method for ImageTrunkDataOut

    public void updateMetadata(ImageName name, ImageDescription description, Actor actor) {
        ImagesBehavior.ensureActive(this.productBooleans.softDeleted());
        var patch = ImagesBehavior.evaluateMetadataUpdate(name, description, actor);

        this.applyChange(actor,
                new ImageMetadataUpdatedEvent(this.imageUuId, patch.name(), actor),
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
                new ImageArchivedEvent(imageUuId, actor),
                () -> this.productBooleans = new ProductBooleans(true, this.productBooleans.softDeleted())
        );
    }

    public void unarchive(Actor actor) {
        ImagesBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new ImageUnarchivedEvent(imageUuId, actor),
                () -> this.productBooleans = new ProductBooleans(false, this.productBooleans.softDeleted())
        );
    }

    public void softDelete(Actor actor) {
        ImagesBehavior.ensureActive(this.productBooleans.softDeleted());
        ImagesBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new ImageSoftDeletedEvent(this.imageUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), true)
        );
    }

    public void restore(Actor actor) {
        if (!this.productBooleans.softDeleted()) return;
        ImagesBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new ImageRestoredEvent(this.imageUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), false)
        );
    }


    // Getters
    public boolean isDeleted() { return productBooleans.softDeleted(); }
    public boolean isArchived() { return productBooleans.archived(); }
    public ProductBooleans getProductBooleans() { return productBooleans; }
    public ImageId getImageId() { return imageId; }
    public ImageUuId getImageUuId() { return imageUuId; }
    public ImageBusinessUuId getImageBusinessUuId() { return imageBusinessUuId; }
    public ImageName getImageName() { return imageName; }
    public ImageDescription getImageDescription() { return imageDescription; }
    public ImageUrl getImageUrl() { return imageUrl; }
}

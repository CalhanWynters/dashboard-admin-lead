package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.ImageUrl;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.events.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

public class ImageAggregate extends BaseAggregateRoot<ImageAggregate> {

    private final ImageId imageId;
    private final ImageUuId imageUuId;
    private final ImageBusinessUuId imageBusinessUuId;
    private final ImageUrl imageUrl;
    private boolean isArchived;

    private ImageName imageName;
    private ImageDescription imageDescription;

    public ImageAggregate(ImageId imageId,
                          ImageUuId imageUuId,
                          ImageBusinessUuId imageBusinessUuId,
                          ImageName imageName,
                          ImageDescription imageDescription,
                          ImageUrl imageUrl,
                          boolean isArchived,
                          AuditMetadata auditMetadata) {
        super(auditMetadata);
        this.imageId = DomainGuard.notNull(imageId, "Image PK ID");
        this.imageUuId = DomainGuard.notNull(imageUuId, "Image UUID");
        this.imageBusinessUuId = DomainGuard.notNull(imageBusinessUuId, "Image Business UUID");
        this.imageName = DomainGuard.notNull(imageName, "Image Name");
        this.imageDescription = DomainGuard.notNull(imageDescription, "Image Description");
        this.imageUrl = DomainGuard.notNull(imageUrl, "Image URL");
        this.isArchived = isArchived;
    }

    public static ImageAggregate create(ImageUuId uuId, ImageBusinessUuId bUuId, ImageName name,
                                        ImageDescription desc, ImageUrl url, Actor actor) {
        ImagesBehavior.verifyCreationAuthority(actor);

        ImageAggregate aggregate = new ImageAggregate(
                null, uuId, bUuId, name, desc, url, false, AuditMetadata.create(actor)
        );
        aggregate.registerEvent(new ImageUploadedEvent(uuId, url, actor));
        return aggregate;
    }

    public void updateMetadata(ImageName name, ImageDescription description, Actor actor) {
        // Line 1: Pure Logic (Security + Invariants)
        var patch = ImagesBehavior.evaluateMetadataUpdate(name, description, actor);

        // Line 2: Execution
        this.applyChange(actor,
                new ImageMetadataUpdatedEvent(this.imageUuId, patch.name(), actor),
                () -> {
                    this.imageName = patch.name();
                    this.imageDescription = patch.description();
                }
        );
    }

    public void changeAltText(ImageDescription newDescription, Actor actor) {
        // Line 1: Pure Logic
        var validatedDescription = ImagesBehavior.evaluateAltTextChange(this.imageDescription, newDescription, actor);

        this.applyChange(actor,
                new ImageAltTextChangedEvent(imageUuId, this.imageDescription, validatedDescription, actor),
                () -> this.imageDescription = validatedDescription
        );
    }

    public void archive(Actor actor) {
        // Line 1: Pure Logic
        ImagesBehavior.verifyArchivable(this.isArchived, actor);

        this.applyChange(actor, new ImageArchivedEvent(imageUuId, actor), () -> this.isArchived = true);
    }

    public void recordReference(String entityType, UuId entityId, Actor actor) {
        // Line 1: Authority check
        ImagesBehavior.verifyReferenceAuthority(actor);

        this.applyChange(actor, new ImageReferencedEvent(this.imageUuId, entityType, entityId, actor), null);
    }

    public void softDelete(Actor actor) {
        // Line 1: Logic
        ImagesBehavior.verifyDeletable(actor);

        this.applyChange(actor, new ImageSoftDeletedEvent(this.imageUuId, actor), () -> {
            // logic for soft delete state
        });
    }

    public void hardDelete(Actor actor) {
        // Line 1: Auth check
        ImagesBehavior.verifyHardDeleteAuthority(actor);

        this.applyChange(actor, new ImageHardDeletedEvent(this.imageUuId, actor), null);
    }

    // Getters
    public ImageId getImageId() { return imageId; }
    public ImageUuId getImageUuId() { return imageUuId; }
    public ImageBusinessUuId getImageBusinessUuId() { return imageBusinessUuId; }
    public ImageName getImageName() { return imageName; }
    public ImageDescription getImageDescription() { return imageDescription; }
    public ImageUrl getImageUrl() { return imageUrl; }
}

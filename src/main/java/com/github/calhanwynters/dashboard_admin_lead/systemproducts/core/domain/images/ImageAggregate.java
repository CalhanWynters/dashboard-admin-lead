package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.ImageUrl;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.*;

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
        // Added 'false' as the 7th argument, before AuditMetadata
        ImageAggregate aggregate = new ImageAggregate(
                null, uuId, bUuId, name, desc, url, false, AuditMetadata.create(actor)
        );
        aggregate.registerEvent(new ImageUploadedEvent(uuId, url, actor));
        return aggregate;
    }


    // --- DOMAIN ACTIONS (Two-Liner Pattern) ---

    public void updateMetadata(ImageName name, ImageDescription description, Actor actor) {
        // Line 1: Pure Logic
        var patch = ImagesBehavior.evaluateMetadataUpdate(name, description);

        // Line 2: Side-Effect Execution
        this.applyChange(actor,
                new ImageMetadataUpdatedEvent(this.imageUuId, patch.name(), actor),
                () -> {
                    this.imageName = patch.name();
                    this.imageDescription = patch.description();
                }
        );
    }

    public void changeAltText(ImageDescription newDescription, Actor actor) {
        var validatedDescription = ImagesBehavior.evaluateAltTextChange(this.imageDescription, newDescription);

        this.applyChange(actor,
                new ImageAltTextChangedEvent(imageUuId, this.imageDescription, validatedDescription, actor),
                () -> this.imageDescription = validatedDescription
        );
    }

    public void archive(Actor actor) {
        // Assuming an 'isArchived' boolean field exists in your state
        ImagesBehavior.verifyArchivable(this.isArchived);

        this.applyChange(actor, new ImageArchivedEvent(imageUuId, actor), () -> this.isArchived = true);
    }

    public void recordReference(String entityType, UuId entityId, Actor actor) {
        this.applyChange(
                actor, // 1
                new ImageReferencedEvent(this.imageUuId, entityType, entityId, actor), // 2
                null // 3: The missing Runnable mutation
        );
    }


    public void softDelete(Actor actor) {
        ImagesBehavior.verifyDeletable();
        this.applyChange(actor, new ImageSoftDeletedEvent(this.imageUuId, actor), () -> {
            // Optional: this.active = false;
        });
    }

    public void hardDelete(Actor actor) {
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

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.ImageUrl;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.*;

/**
 * Aggregate Root for Image assets.
 * Encapsulates metadata updates and ensures mandatory audit attribution.
 */
public class ImageAggregate extends BaseAggregateRoot<ImageAggregate> {

    private final ImageId imageId;
    private final ImageUuId imageUuId;
    private final ImageBusinessUuId imageBusinessUuId;
    private final ImageUrl imageUrl;

    private ImageName imageName;
    private ImageDescription imageDescription;

    public ImageAggregate(ImageId imageId,
                          ImageUuId imageUuId,
                          ImageBusinessUuId imageBusinessUuId,
                          ImageName imageName,
                          ImageDescription imageDescription,
                          ImageUrl imageUrl,
                          AuditMetadata auditMetadata) {
        super(auditMetadata);

        DomainGuard.notNull(imageId, "Image PK ID");
        DomainGuard.notNull(imageUuId, "Image UUID");
        DomainGuard.notNull(imageBusinessUuId, "Image Business UUID");
        DomainGuard.notNull(imageName, "Image Name");
        DomainGuard.notNull(imageDescription, "Image Description");
        DomainGuard.notNull(imageUrl, "Image URL");

        this.imageId = imageId;
        this.imageUuId = imageUuId;
        this.imageBusinessUuId = imageBusinessUuId;
        this.imageName = imageName;
        this.imageDescription = imageDescription;
        this.imageUrl = imageUrl;
    }

    /**
     * Atomically updates image metadata and refreshes the audit trail.
     */
    public void updateMetadata(ImageName name, ImageDescription description, Actor actor) {
        DomainGuard.notNull(name, "New Image Name");
        DomainGuard.notNull(description, "New Image Description");
        DomainGuard.notNull(actor, "Actor performing the update");

        this.imageName = name;
        this.imageDescription = description;

        this.recordUpdate(actor);
    }

    // Getters
    public ImageId getImageId() { return imageId; }
    public ImageUuId getImageUuId() { return imageUuId; }
    public ImageBusinessUuId getImageBusinessUuId() { return imageBusinessUuId; }
    public ImageName getImageName() { return imageName; }
    public ImageDescription getImageDescription() { return imageDescription; }
    public ImageUrl getImageUrl() { return imageUrl; }
}

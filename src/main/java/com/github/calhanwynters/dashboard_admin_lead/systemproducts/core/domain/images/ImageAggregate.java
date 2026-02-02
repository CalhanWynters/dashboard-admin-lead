package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images;

import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.ImageUrl;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.*;

public class ImageAggregate extends BaseAggregateRoot<ImageAggregate> {

    private final ImageId imageId;
    private final ImageUuId imageUuId;
    private final ImageBusinessUuId imageBusinessUuId;
    private final ImageName imageName;
    private final ImageDescription imageDescription;
    private final ImageUrl imageUrl;

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

    // Getters
    public ImageId getImageId() { return imageId; }
    public ImageUuId getImageUuId() { return imageUuId; }
    public ImageBusinessUuId getImageBusinessUuId() { return imageBusinessUuId; }
    public ImageName getImageName() { return imageName; }
    public ImageDescription getImageDescription() { return imageDescription; }
    public ImageUrl getImageUrl() { return imageUrl; }
}

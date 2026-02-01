package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.ImageId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.ImageUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.ImageBusinessUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.ImageName;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.ImageDescription;

import com.github.calhanwynters.dashboard_admin_lead.common.ImageUrl;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import org.springframework.data.domain.AbstractAggregateRoot;

public class ImageAggregate extends AbstractAggregateRoot<ImageAggregate> {

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
                          ImageUrl imageUrl) {
        // Validation checks
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
    public ImageId getImageId() {
        return imageId;
    }

    public ImageUuId getImageUuId() {
        return imageUuId;
    }

    public ImageBusinessUuId getImageBusinessUuId() {
        return imageBusinessUuId;
    }

    public ImageName getImageName() {
        return imageName;
    }

    public ImageDescription getImageDescription() {
        return imageDescription;
    }

    public ImageUrl getImageUrl() {
        return imageUrl;
    }
}

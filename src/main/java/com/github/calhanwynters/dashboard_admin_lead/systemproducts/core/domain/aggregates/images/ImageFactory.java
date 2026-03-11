package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleansLEGACY;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

public class ImageFactory {
    public static ImageAggregateLEGACY create(ImagesBusinessUuId bizId, ImageName name, ImageDescription desc, ImageUrl url, Actor creator) {
        // 'url' is now recognized as ImagesDomainWrapper.ImageUrl due to the static import
        return ImageAggregateLEGACY.create(ImageUuId.generate(), bizId, name, desc, url, creator);
    }

    public static ImageAggregateLEGACY reconstitute(ImageId id, ImageUuId uuId, ImagesBusinessUuId bizId, ImageName name,
                                                    ImageDescription desc, ImageUrl url, ProductBooleansLEGACY booleans, AuditMetadata auditMetadata) {
        return new ImageAggregateLEGACY(id, uuId, bizId, name, desc, url, booleans, auditMetadata);
    }
}

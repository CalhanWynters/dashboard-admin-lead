package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

public class ImageFactory {
    public static ImageAggregate create(ImageBusinessUuId bizId, ImageName name, ImageDescription desc, ImageUrl url, Actor creator) {
        // 'url' is now recognized as ImagesDomainWrapper.ImageUrl due to the static import
        return ImageAggregate.create(ImageUuId.generate(), bizId, name, desc, url, creator);
    }

    public static ImageAggregate reconstitute(ImageId id, ImageUuId uuId, ImageBusinessUuId bizId, ImageName name,
                                              ImageDescription desc, ImageUrl url, ProductBooleans booleans, AuditMetadata auditMetadata) {
        return new ImageAggregate(id, uuId, bizId, name, desc, url, booleans, auditMetadata);
    }
}

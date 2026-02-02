package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.ImageUrl;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.*;

public class ImageFactory {

    public static ImageAggregate create(
            ImageBusinessUuId bizId,
            ImageName name,
            ImageDescription desc,
            ImageUrl url,
            Actor creator) {
        return new ImageAggregate(
                ImageId.of(0L),
                ImageUuId.generate(),
                bizId,
                name,
                desc,
                url,
                AuditMetadata.create(creator)
        );
    }

    public static ImageAggregate reconstitute(
            ImageId id, ImageUuId uuId, ImageBusinessUuId bizId,
            ImageName name, ImageDescription desc, ImageUrl url,
            AuditMetadata auditMetadata) {
        return new ImageAggregate(id, uuId, bizId, name, desc, url, auditMetadata);
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.*;

public class GalleryFactory {

    public static GalleryAggregate create(GalleryBusinessUuId bizId, Actor creator) {
        return new GalleryAggregate(
                GalleryId.of(0L),
                GalleryUuId.generate(),
                bizId,
                AuditMetadata.create(creator)
        );
    }

    public static GalleryAggregate reconstitute(
            GalleryId id,
            GalleryUuId uuId,
            GalleryBusinessUuId bizId,
            AuditMetadata auditMetadata) {
        return new GalleryAggregate(id, uuId, bizId, auditMetadata);
    }
}

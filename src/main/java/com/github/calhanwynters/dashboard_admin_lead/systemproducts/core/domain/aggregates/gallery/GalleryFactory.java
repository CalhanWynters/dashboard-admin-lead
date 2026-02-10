package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

import java.util.List;

public class GalleryFactory {
    public static GalleryAggregate create(GalleryBusinessUuId bizId, Actor creator) {
        return new GalleryAggregate(
                null, GalleryUuId.generate(), bizId, false, List.of(),
                new ProductBooleans(false, false), AuditMetadata.create(creator)
        );
    }

    public static GalleryAggregate reconstitute(GalleryId id, GalleryUuId uuId, GalleryBusinessUuId bizId,
                                                boolean isPublic, List<ImageUuId> imageUuIds, ProductBooleans booleans, AuditMetadata auditMetadata) {
        return new GalleryAggregate(id, uuId, bizId, isPublic, imageUuIds, booleans, auditMetadata);
    }
}


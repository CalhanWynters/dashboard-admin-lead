package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleansLEGACY;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

import java.util.List;

public class GalleryFactory {
    public static GalleryAggregateLEGACY create(GalleryBusinessUuId bizId, Actor creator) {
        return new GalleryAggregateLEGACY(
                null, GalleryUuId.generate(), bizId, false, List.of(),
                new ProductBooleansLEGACY(false, false), AuditMetadata.create(creator)
        );
    }

    public static GalleryAggregateLEGACY reconstitute(GalleryId id, GalleryUuId uuId, GalleryBusinessUuId bizId,
                                                      boolean isPublic, List<ImageUuId> imageUuIds, ProductBooleansLEGACY booleans, AuditMetadata auditMetadata) {
        return new GalleryAggregateLEGACY(id, uuId, bizId, isPublic, imageUuIds, booleans, auditMetadata);
    }
}


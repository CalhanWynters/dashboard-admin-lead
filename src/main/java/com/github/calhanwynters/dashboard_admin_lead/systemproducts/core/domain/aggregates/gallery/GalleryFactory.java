package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

import java.util.List;

public class GalleryFactory {

    /**
     * Creates a brand new Gallery aggregate.
     * Initial state is always private with an empty image list.
     */
    public static GalleryAggregate create(GalleryBusinessUuId bizId, Actor creator) {
        return new GalleryAggregate(
                null, // PK ID is null for new entities
                GalleryUuId.generate(),
                bizId,
                false, // Default publicity
                List.of(), // Initial empty collection
                AuditMetadata.create(creator)
        );
    }

    /**
     * Reconstitutes an existing Gallery from persistence.
     * Bypasses business rules to restore known state.
     */
    public static GalleryAggregate reconstitute(
            GalleryId id,
            GalleryUuId uuId,
            GalleryBusinessUuId bizId,
            boolean isPublic,
            List<ImageUuId> imageUuIds,
            AuditMetadata auditMetadata) {
        return new GalleryAggregate(id, uuId, bizId, isPublic, imageUuIds, auditMetadata);
    }
}

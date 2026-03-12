package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Refactored Gallery Factory (2026 Edition).
 * Handles creation and reconstitution with standardized LifecycleState and versioning.
 */
public class GalleryFactory {

    private GalleryFactory() {}

    /**
     * Creation Factory
     * Initializes a new Gallery with a generated UUID and fresh audit trail.
     */
    public static GalleryAggregate create(GalleryBusinessUuId bizId, Actor creator) {
        GalleryUuId newUuId = GalleryUuId.generate();

        // SOC 2: Authority check via Behavior before instantiation
        GalleryBehavior.validateCreation(newUuId, bizId, creator);

        return new GalleryAggregate(
                null,
                newUuId,
                bizId,
                false,            // isPublic (default)
                List.of(),        // imageUuIds (initial)
                AuditMetadata.create(creator),
                new LifecycleState(false, false),
                0L,               // optLockVer
                1,                // schemaVer
                null              // lastSyncedAt
        );
    }

    /**
     * Reconstitution Factory
     * Restores the full state from persistence, including versioning and sync data.
     */
    public static GalleryAggregate reconstitute(
            GalleryId id,
            GalleryUuId uuId,
            GalleryBusinessUuId bizId,
            boolean isPublic,
            List<ImageUuId> imageUuIds,
            AuditMetadata auditMetadata,
            LifecycleState lifecycleState,
            Long optLockVer,
            Integer schemaVer,
            OffsetDateTime lastSyncedAt) {

        return new GalleryAggregate(
                id, uuId, bizId, isPublic, imageUuIds,
                auditMetadata, lifecycleState,
                optLockVer, schemaVer, lastSyncedAt
        );
    }
}

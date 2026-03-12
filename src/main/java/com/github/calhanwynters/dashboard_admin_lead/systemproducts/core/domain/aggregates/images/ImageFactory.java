package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

import java.time.OffsetDateTime;

/**
 * Refactored Image Factory (2026 Edition).
 * Handles creation and reconstitution with standardized LifecycleState and versioning.
 */
public class ImageFactory {

    private ImageFactory() {}

    /**
     * Creation Factory
     * Initializes a new Image with a generated UUID and fresh audit trail.
     */
    public static ImageAggregate create(ImagesBusinessUuId bizId, ImageName name,
                                        ImageDescription desc, ImageUrl url, Actor creator) {

        ImageUuId newUuId = ImageUuId.generate();

        // SOC 2: Authority check via Behavior before instantiation
        ImagesBehavior.validateCreation(newUuId, bizId, creator);

        return new ImageAggregate(
                null,
                newUuId,
                bizId,
                name,
                desc,
                url,
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
    public static ImageAggregate reconstitute(
            ImageId id,
            ImageUuId uuId,
            ImagesBusinessUuId bizId,
            ImageName name,
            ImageDescription desc,
            ImageUrl url,
            AuditMetadata auditMetadata,
            LifecycleState lifecycleState,
            Long optLockVer,
            Integer schemaVer,
            OffsetDateTime lastSyncedAt) {

        return new ImageAggregate(
                id, uuId, bizId, name, desc, url,
                auditMetadata, lifecycleState,
                optLockVer, schemaVer, lastSyncedAt
        );
    }
}

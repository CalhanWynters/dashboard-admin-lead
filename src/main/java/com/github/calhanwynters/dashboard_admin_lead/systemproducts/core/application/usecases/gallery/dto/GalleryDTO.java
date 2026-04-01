package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryAggregate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Data Transfer Object for Gallery Aggregate.
 * Handles the collection of image identifiers and visibility state.
 */
public record GalleryDTO(
        UUID uuid,
        String businessUuid,
        boolean isPublic,
        List<UUID> imageUuids,
        boolean isArchived,
        boolean isSoftDeleted,
        Long version,
        OffsetDateTime lastSyncedAt
) {
    /**
     * Factory method to map from the Domain Aggregate to the DTO.
     */
    public static GalleryDTO fromAggregate(GalleryAggregate aggregate) {
        return new GalleryDTO(
                // 1. GalleryUuId -> UuId -> java.util.UUID
                aggregate.getUuId().value().asUUID(),

                // 2. GalleryBusinessUuId -> UuId -> String
                aggregate.getBusinessUuId().value().value(),

                aggregate.isPublic(),

                // 3. List<ImageUuId> -> List<UUID>
                aggregate.getImageUuIds().stream()
                        .map(imageUuId -> imageUuId.value().asUUID())
                        .toList(),

                // 4. LifecycleState accessors
                aggregate.getLifecycleState().archived(),
                aggregate.getLifecycleState().softDeleted(),

                aggregate.getOptLockVer(),
                aggregate.getLastSyncedAt()
        );
    }
}

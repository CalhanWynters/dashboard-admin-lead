package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.dto;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImageAggregate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Image Aggregate.
 * Flattens nested Domain Records into standard Java types.
 */
public record ImagesDTO(
        UUID uuid,
        String businessUuid,
        String name,
        String description,
        String url,
        boolean isArchived,
        boolean isSoftDeleted,
        Long version,
        OffsetDateTime lastSyncedAt
) {
    /**
     * Factory method to map from the Domain Aggregate to the DTO.
     */
    public static ImagesDTO fromAggregate(ImageAggregate aggregate) {
        return new ImagesDTO(
                // 1. ImageUuId (Record) -> UuId (Record) -> java.util.UUID
                aggregate.getUuId().value().asUUID(),

                // 2. ImagesBusinessUuId (Record) -> UuId (Record) -> String
                aggregate.getBusinessUuId().value().value(),

                // 3. ImageName (Record) -> Name (Record) -> String
                aggregate.getImageName().value().value(),

                // 4. ImageDescription (Record) -> Description (Record) -> String
                // Note: Change .value() to .text() here
                aggregate.getImageDescription().value().text(),

                // 5. ImageUrl (Record) -> String
                aggregate.getImageUrl().value(),

                // 6. LifecycleState (Record)
                aggregate.getLifecycleState().archived(),
                aggregate.getLifecycleState().softDeleted(),

                aggregate.getOptLockVer(),
                aggregate.getLastSyncedAt()
        );
    }

}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.dto;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesAggregate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Feature Aggregate.
 * Flattens domain objects for consumer use.
 */
public record FeatureDTO(
        UUID uuid,
        String businessUuid,
        String name,
        String compatibilityTag,
        boolean isArchived,
        boolean isSoftDeleted,
        Long version,
        OffsetDateTime lastSyncedAt
) {
    /**
     * Factory method to map from the Domain Aggregate to the DTO.
     */
    public static FeatureDTO fromAggregate(FeaturesAggregate aggregate) {
        return new FeatureDTO(
                // 1. FeatureUuId (Record) -> UuId (Record) -> java.util.UUID
                aggregate.getUuId().value().asUUID(),

                // 2. FeatureBusinessUuId (Record) -> UuId (Record) -> String
                aggregate.getBusinessUuId().value().value(),

                // 3. FeatureName (Record) -> Name (Record) -> String
                aggregate.getFeaturesName().value().value(),

                // 4. FeatureLabel (Record) -> Label (Record) -> String
                aggregate.getCompatibilityTag().value().value(),

                // 5. LifecycleState (Record) -> boolean
                aggregate.getLifecycleState().archived(),
                aggregate.getLifecycleState().softDeleted(),

                // 6. Metadata
                aggregate.getOptLockVer(),
                aggregate.getLastSyncedAt()
        );
    }



}

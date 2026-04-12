package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for Variants Aggregate.
 * Flattens variant identity and assigned feature sets.
 */
public record VariantsDTO(
        UUID uuid,
        String businessUuid,
        String name,
        String region,
        Set<UUID> featureUuids,
        boolean isArchived,
        boolean isSoftDeleted,
        Long version,
        OffsetDateTime lastSyncedAt
) {
    /**
     * Factory method to map from the Domain Aggregate to the DTO.
     */
    public static VariantsDTO fromAggregate(VariantsAggregate aggregate) {
        return new VariantsDTO(
                // 1. VariantsUuId -> UuId -> java.util.UUID
                aggregate.getUuId().value().asUUID(),

                // 2. VariantsBusinessUuId -> UuId -> String
                aggregate.getBusinessUuId().value().value(),

                // 3. VariantsName -> Name -> String
                aggregate.getVariantsName().value().value(),

                // 4. VariantsRegion -> Region -> String
                aggregate.getVariantsRegion().value().value(),

                // 5. Set<FeatureUuId> -> Set<UUID>
                aggregate.getAssignedFeatureUuIds().stream()
                        .map(fUuId -> fUuId.value().asUUID())
                        .collect(Collectors.toUnmodifiableSet()),

                // 6. LifecycleState accessors
                aggregate.getLifecycleState().archived(),
                aggregate.getLifecycleState().softDeleted(),

                aggregate.getOptLockVer(),
                aggregate.getLastSyncedAt()
        );
    }
}

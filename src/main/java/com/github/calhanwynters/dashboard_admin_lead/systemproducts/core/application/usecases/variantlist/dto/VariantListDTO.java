package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.dto;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListAggregate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for VariantList Aggregate.
 * Flattens the Set of Variant identifiers and lifecycle state.
 */
public record VariantListDTO(
        UUID uuid,
        String businessUuid,
        Set<UUID> variantUuids,
        boolean isArchived,
        boolean isSoftDeleted,
        Long version,
        OffsetDateTime lastSyncedAt
) {
    /**
     * Factory method to map from the Domain Aggregate to the DTO.
     */
    public static VariantListDTO fromAggregate(VariantListAggregate aggregate) {
        return new VariantListDTO(
                // 1. VariantListUuId -> UuId -> java.util.UUID
                aggregate.getUuId().value().asUUID(),

                // 2. VariantListBusinessUuId -> UuId -> String
                aggregate.getBusinessUuId().value().value(),

                // 3. Set<VariantsUuId> -> Set<UUID>
                aggregate.getVariantUuIds().stream()
                        .map(vUuId -> vUuId.value().asUUID())
                        .collect(Collectors.toUnmodifiableSet()),

                // 4. LifecycleState record accessors
                aggregate.getLifecycleState().archived(),
                aggregate.getLifecycleState().softDeleted(),

                aggregate.getOptLockVer(),
                aggregate.getLastSyncedAt()
        );
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.dto;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListAggregate;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for TypeList Aggregate.
 * Flattens the Set of Type identifiers for API consumers.
 */
public record TypeListDTO(
        UUID uuid,
        String businessUuid,
        Set<UUID> typeUuids,
        boolean isArchived,
        boolean isSoftDeleted,
        Long version,
        OffsetDateTime lastSyncedAt
) {
    /**
     * Factory method to map from the Domain Aggregate to the DTO.
     */
    public static TypeListDTO fromAggregate(TypeListAggregate aggregate) {
        return new TypeListDTO(
                // 1. TypeListUuId -> UuId -> java.util.UUID
                aggregate.getUuId().value().asUUID(),

                // 2. TypeListBusinessUuId -> UuId -> String
                aggregate.getBusinessUuId().value().value(),

                // 3. Set<TypesUuId> -> Set<UUID>
                aggregate.getTypeUuIds().stream()
                        .map(typesUuId -> typesUuId.value().asUUID())
                        .collect(Collectors.toUnmodifiableSet()),

                // 4. LifecycleState components
                aggregate.getLifecycleState().archived(),
                aggregate.getLifecycleState().softDeleted(),

                aggregate.getOptLockVer(),
                aggregate.getLastSyncedAt()
        );
    }
}

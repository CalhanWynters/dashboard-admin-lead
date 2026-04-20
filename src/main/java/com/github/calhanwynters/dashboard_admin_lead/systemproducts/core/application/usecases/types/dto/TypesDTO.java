package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.PhysicalSpecs;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Finalized Types DTO (2026 SOC 2 Edition).
 * Correctly maps hardened BigDecimal Dimensions and Weights.
 */
public record TypesDTO(
        UUID uuid,
        String businessUuid,
        String name,
        String region,
        PhysicalSpecsData specs,
        boolean isArchived,
        boolean isSoftDeleted,
        Long version,
        OffsetDateTime lastSyncedAt
) {
    public static TypesDTO fromAggregate(TypesAggregate aggregate) {
        return new TypesDTO(
                aggregate.getUuId().value().asUUID(),
                aggregate.getBusinessUuId().value().value(),
                aggregate.getTypesName().value().value(),
                aggregate.getTypesRegion().value().value(),
                mapSpecs(aggregate.getTypesPhysicalSpecs().value()),
                aggregate.getLifecycleState().archived(),
                aggregate.getLifecycleState().softDeleted(),
                aggregate.getOptLockVer(),
                aggregate.getLastSyncedAt()
        );
    }

    private static PhysicalSpecsData mapSpecs(PhysicalSpecs specs) {
        return new PhysicalSpecsData(
                // Weight components
                specs.weight().amount(),
                specs.weight().weightUnit().name(),

                // Dimensions components (BigDecimal accessors)
                specs.dimensions().length(),
                specs.dimensions().width(),
                specs.dimensions().height(),
                specs.dimensions().sizeUnit().name(),

                // CareInstruction component
                specs.careInstructions().instructions()
        );
    }

    /**
     * Flattened data structure for high-precision physical specs.
     */
    public record PhysicalSpecsData(
            BigDecimal weightAmount,
            String weightUnit,
            BigDecimal length,
            BigDecimal width,
            BigDecimal height,
            String sizeUnit,
            String careInstructions
    ) {}
}

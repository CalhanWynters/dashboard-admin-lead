package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.PhysicalSpecs;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesPhysicalSpecs;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO for editing a Type's Physical Specifications.
 * Maps high-precision inputs to hardened Domain records for SOC 2 compliance.
 */
public record TypeEditPhysicalSpecsDTO(
        BigDecimal weightAmount,
        String weightUnit,
        BigDecimal length,
        BigDecimal width,
        BigDecimal height,
        String dimensionUnit,
        String careInstructions,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps raw inputs into a hardened TypesPhysicalSpecs record.
     * Triggers DomainGuard validation for BigDecimal scale and range.
     */
    public TypesPhysicalSpecs toTypesPhysicalSpecs() {
        Weight weight = new Weight(weightAmount, WeightUnitEnums.valueOf(weightUnit.toUpperCase()));
        Dimensions dimensions = new Dimensions(
                length, width, height,
                DimensionUnitEnums.valueOf(dimensionUnit.toUpperCase())
        );
        CareInstruction care = new CareInstruction(careInstructions);

        return new TypesPhysicalSpecs(new PhysicalSpecs(weight, dimensions, care));
    }

    /**
     * Reconstructs the Actor for aggregate authorization and event attribution.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.application.usecases.type;

import java.util.List;

public record TypeColProjectionDTO(
        String typeColId,
        String businessId,
        List<TypeDTO> types
) {
    public record TypeDTO(
            String typeId,
            String compatibilityTag,
            String typeName,
            DimensionsDTO typeDimensions,
            WeightDTO typeWeight,
            String typeDescription,
            String typeCareInstruction
    ) {}

    public record DimensionsDTO(double length, double width, double height) {}
    public record WeightDTO(double amount) {}
}

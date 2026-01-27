package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.mappers;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.dto.TypeColProjectionDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.type.Type;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.type.TypeCollection;

import java.util.stream.Collectors;

public class TypeColProjectionMapper {

    public static TypeColProjectionDTO toDto(TypeCollection collection) {
        if (collection == null) return null;

        return new TypeColProjectionDTO(
                collection.getTypeColId().value(),
                collection.getBusinessId().value(),
                collection.getTypes().stream()
                        .map(TypeColProjectionMapper::mapType)
                        .collect(Collectors.toList())
        );
    }

    private static TypeColProjectionDTO.TypeDTO mapType(Type type) {
        return new TypeColProjectionDTO.TypeDTO(
                type.typeId().value(),
                type.compatibilityTag().value(), // From Label
                type.typeName().value(),         // From Name
                type.typeDimensions() != null ? new TypeColProjectionDTO.DimensionsDTO(
                        type.typeDimensions().length().doubleValue(),
                        type.typeDimensions().width().doubleValue(),
                        type.typeDimensions().height().doubleValue()
                ) : null,
                type.typeWeight() != null ? new TypeColProjectionDTO.WeightDTO(
                        type.typeWeight().amount().doubleValue()
                ) : null,
                type.typeDescription().text(),    // From Description
                type.typeCareInstruction().instructions() // From CareInstruction
        );
    }
}

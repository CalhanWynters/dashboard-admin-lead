package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.dto.TypeListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListAggregate;
import java.util.List;

public final class TypeListMapper {
    private TypeListMapper() {}

    public static TypeListDTO toDto(TypeListAggregate aggregate) {
        if (aggregate == null) return null;
        return TypeListDTO.fromAggregate(aggregate);
    }

    public static List<TypeListDTO> toDtoList(List<TypeListAggregate> aggregates) {
        if (aggregates == null) return List.of();
        return aggregates.stream().map(TypeListMapper::toDto).toList();
    }
}

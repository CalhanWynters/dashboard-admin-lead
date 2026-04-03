package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.types.dto.TypesDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Assembler for Types-related transformations.
 * Centralizes collection mapping to keep Use Case logic lean and SOC 2 compliant.
 */
public final class TypesMapper {

    private TypesMapper() {
        // Private constructor to prevent instantiation of utility class
    }

    /**
     * Maps a list of Domain Aggregates to a list of DTOs.
     * Useful for Search and List use cases.
     */
    public static List<TypesDTO> toDtoList(List<TypesAggregate> aggregates) {
        if (aggregates == null) {
            return List.of();
        }
        return aggregates.stream()
                .map(TypesDTO::fromAggregate)
                .collect(Collectors.toList());
    }
}

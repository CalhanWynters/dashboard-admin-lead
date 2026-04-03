package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variants.dto.VariantsDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Assembler for Variants-related transformations.
 * Centralizes collection mapping to keep Use Case logic decoupled from DTO internals.
 */
public final class VariantsMapper {

    private VariantsMapper() {
        // Private constructor to prevent instantiation of utility class
    }

    /**
     * Maps a single Domain Aggregate to a flattened DTO.
     */
    public static VariantsDTO toDto(VariantsAggregate aggregate) {
        if (aggregate == null) {
            return null;
        }
        return VariantsDTO.fromAggregate(aggregate);
    }

    /**
     * Maps a list of Domain Aggregates to a list of DTOs.
     * Essential for search results and dashboard views.
     */
    public static List<VariantsDTO> toDtoList(List<VariantsAggregate> aggregates) {
        if (aggregates == null) {
            return List.of();
        }
        return aggregates.stream()
                .map(VariantsMapper::toDto)
                .collect(Collectors.toList());
    }
}

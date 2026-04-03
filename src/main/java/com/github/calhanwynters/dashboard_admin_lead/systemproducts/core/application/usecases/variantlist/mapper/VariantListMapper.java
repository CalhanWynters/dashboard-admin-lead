package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.dto.VariantListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListAggregate;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Assembler for VariantList-related transformations.
 * Centralizes collection mapping to keep Use Case logic decoupled from DTO internals.
 */
public final class VariantListMapper {

    private VariantListMapper() {
        // Private constructor to prevent instantiation of utility class
    }

    /**
     * Maps a single Domain Aggregate to a flattened DTO.
     */
    public static VariantListDTO toDto(VariantListAggregate aggregate) {
        if (aggregate == null) {
            return null;
        }
        return VariantListDTO.fromAggregate(aggregate);
    }

    /**
     * Maps a list of Domain Aggregates to a list of DTOs.
     * Essential for search results and dashboard views.
     */
    public static List<VariantListDTO> toDtoList(List<VariantListAggregate> aggregates) {
        if (aggregates == null) {
            return List.of();
        }
        return aggregates.stream()
                .map(VariantListMapper::toDto)
                .collect(Collectors.toList());
    }
}

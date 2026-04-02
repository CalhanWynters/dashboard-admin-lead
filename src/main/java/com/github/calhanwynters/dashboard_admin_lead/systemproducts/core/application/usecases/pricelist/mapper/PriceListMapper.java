package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto.PriceListDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListAggregate;

import java.util.List;

/**
 * High-performance mapper for PriceList Read-Models.
 * Bridges the hardened Multi-Currency Aggregate and the Application DTO.
 */
public final class PriceListMapper {

    private PriceListMapper() { } // Static utility only

    /**
     * Maps a single PriceListAggregate to a serializable PriceListDTO.
     * Extracts raw values from Value Objects and flattens the Pricing Matrix.
     */
    public static PriceListDTO toDto(PriceListAggregate aggregate) {
        if (aggregate == null) return null;
        return PriceListDTO.fromAggregate(aggregate);
    }

    /**
     * Maps a collection of Aggregates to a list of DTOs.
     * Optimized for Search Results or PriceList Catalogs.
     */
    public static List<PriceListDTO> toDtoList(List<PriceListAggregate> aggregates) {
        if (aggregates == null) return List.of();
        return aggregates.stream()
                .map(PriceListMapper::toDto)
                .toList();
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.mapper;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.dto.GalleryDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryAggregate;

import java.util.List;

/**
 * High-performance mapper for Gallery Read-Models.
 * Bridges the gap between the hardened Domain Aggregate and the Application DTO.
 */
public final class GalleryMapper {

    private GalleryMapper() { } // Static utility only

    /**
     * Maps a single GalleryAggregate to a serializable GalleryDTO.
     * Extracts raw values from Value Objects and flattens the Lifecycle state.
     */
    public static GalleryDTO toDto(GalleryAggregate aggregate) {
        if (aggregate == null) return null;
        return GalleryDTO.fromAggregate(aggregate);
    }

    /**
     * Maps a collection of Aggregates to a list of DTOs.
     * Optimized for Search Results or Gallery Listings.
     */
    public static List<GalleryDTO> toDtoList(List<GalleryAggregate> aggregates) {
        if (aggregates == null) return List.of();
        return aggregates.stream()
                .map(GalleryMapper::toDto)
                .toList();
    }
}

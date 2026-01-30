package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryCollection;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.ImageUrl;
import java.util.List;
import java.util.stream.Collectors;

public class GalleryProjectionMapper {

    /**
     * Maps the GalleryCollection Aggregate to a Read-Model DTO.
     * Aligned with 2026 field naming and Value Object extraction.
     */
    public static GalleryProjectionDTO toDto(GalleryCollection collection) {
        if (collection == null) return null;

        // Extract raw strings from the ImageUrl Value Objects for the DTO
        List<String> urls = collection.getImageUrls().stream()
                .map(ImageUrl::url)
                .collect(Collectors.toList());

        return new GalleryProjectionDTO(
                collection.getGalleryColId().value(), // Updated to match Aggregate field
                collection.getBusinessId().value(),
                urls,
                urls.size()
        );
    }

    /**
     * Maps a list of aggregates to a list of DTOs.
     */
    public static List<GalleryProjectionDTO> toDtoList(List<GalleryCollection> collections) {
        if (collections == null) return List.of();

        return collections.stream()
                .map(GalleryProjectionMapper::toDto)
                .collect(Collectors.toList());
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.mappers;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.dto.GalleryProjectionDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.GalleryQueryRepository.GallerySummary;

import java.util.List;
import java.util.stream.Collectors;

public class GalleryProjectionMapper {

    /**
     * Maps the Domain's GallerySummary record to an Application-level DTO.
     */
    public static GalleryProjectionDTO toDto(GallerySummary summary) {
        if (summary == null) return null;

        return new GalleryProjectionDTO(
                summary.galleryUuid(),
                summary.businessId(),
                summary.imageUrls() != null ? summary.imageUrls() : List.of(),
                summary.imageUrls() != null ? summary.imageUrls().size() : 0
        );
    }

    /**
     * Bulk mapping for list responses.
     */
    public static List<GalleryProjectionDTO> toDtoList(List<GallerySummary> summaries) {
        return summaries.stream()
                .map(GalleryProjectionMapper::toDto)
                .collect(Collectors.toList());
    }
}
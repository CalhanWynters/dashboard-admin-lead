package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.dto.GalleryProjectionDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.mappers.GalleryProjectionMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.GalleryQueryRepository;
import java.util.List;
import java.util.Optional;

/**
 * Application Service for Gallery queries.
 * Coordinates between Domain Repositories and Application DTOs.
 */
public class GalleryQueryService {

    private final GalleryQueryRepository queryRepository;

    public GalleryQueryService(GalleryQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    /**
     * Retrieves a single gallery by its UUID and maps it to a DTO.
     */
    public Optional<GalleryProjectionDTO> getGallery(String galleryUuid) {
        return queryRepository.findByUuid(galleryUuid)
                .map(GalleryProjectionMapper::toDto);
    }

    /**
     * Retrieves all galleries associated with a business.
     */
    public List<GalleryProjectionDTO> getGalleriesByBusiness(String businessId) {
        List<GalleryQueryRepository.GallerySummary> summaries =
                queryRepository.findAllByBusiness(businessId);

        return GalleryProjectionMapper.toDtoList(summaries);
    }
}

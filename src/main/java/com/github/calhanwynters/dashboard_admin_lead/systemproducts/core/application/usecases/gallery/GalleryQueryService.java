package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.dto.GalleryProjectionDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.mappers.GalleryProjectionMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.GalleryQueryRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryCollection;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;

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

    public Optional<GalleryProjectionDTO> getGallery(String galleryUuid) {
        // Enforce 2026 Integrity: Validate input via VO before processing
        UuId domainId = UuId.fromString(galleryUuid);
        return queryRepository.findById(domainId)
                .map(GalleryProjectionMapper::toDto);
    }

    public List<GalleryProjectionDTO> getGalleriesByBusiness(String businessId) {
        UuId domainBizId = UuId.fromString(businessId);
        List<GalleryCollection> collections = queryRepository.findAllByBusinessId(domainBizId);
        return GalleryProjectionMapper.toDtoList(collections);
    }
}

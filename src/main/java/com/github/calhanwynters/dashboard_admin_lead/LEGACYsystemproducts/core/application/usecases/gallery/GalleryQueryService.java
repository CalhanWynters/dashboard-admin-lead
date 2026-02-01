package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.application.usecases.gallery;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.GalleryQueryRepository;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.gallery.GalleryCollection;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.UuId;

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

package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.application.usecases.gallery;

import java.util.List;

/**
 * Optimized DTO for frontend consumption.
 */
public record GalleryProjectionDTO(
        String galleryUuid,
        String businessId,
        List<String> imageUrls,
        int totalImages
) {}
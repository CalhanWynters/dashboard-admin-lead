package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain;

import java.util.List;
import java.util.Optional;

/**
 * Domain-facing contract for reading Gallery data.
 * This interface belongs to the Core/Domain layer.
 */
public interface GalleryQueryRepository {

    /**
     * Defines the standard structure for a Gallery read projection
     * without exposing infrastructure-specific classes (like Document).
     */
    record GallerySummary(String galleryUuid, String businessId, List<String> imageUrls) {}

    Optional<GallerySummary> findByUuid(String galleryUuid);

    List<GallerySummary> findAllByBusiness(String businessId);
}

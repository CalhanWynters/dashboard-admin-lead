package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryCollection;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import java.util.List;
import java.util.Optional;

/**
 * Domain-facing contract for Gallery Read operations.
 * Aligned with Variant and Type modules for 2026.
 */
public interface GalleryQueryRepository {
    Optional<GalleryCollection> findById(UuId galleryId);
    List<GalleryCollection> findAllByBusinessId(UuId businessId);
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

import java.util.Optional;
import java.util.List;

public interface GalleryRepository {

    // 1. IDENTITY & BUSINESS LOOKUP
    Optional<GalleryAggregateLEGACY> findByUuId(GalleryUuId galleryUuId);

    Optional<GalleryAggregateLEGACY> findByBusinessUuId(GalleryBusinessUuId businessUuId);

    // 2. RELATIONSHIP QUERY
    // Find which galleries contain a specific image - useful for cleanup/validation
    List<GalleryAggregateLEGACY> findAllByImageUuId(ImageUuId imageUuId);

    // 3. PERSISTENCE
    // This must persist the Gallery state AND the list of ImageUuIds atomically
    void save(GalleryAggregateLEGACY aggregate);

    // 4. COLLECTION QUERIES
    List<GalleryAggregateLEGACY> findAllPublic();

    List<GalleryAggregateLEGACY> findAllActive();

    // 5. LIFECYCLE
    void hardDelete(GalleryUuId galleryUuId);
}

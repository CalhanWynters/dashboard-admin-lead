package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

import java.util.Optional;
import java.util.List;

public interface ImagesRepository {

    // 1. IDENTITY & BUSINESS LOOKUP
    Optional<ImageAggregateLEGACY> findByUuId(ImageUuId imagesUuId);

    Optional<ImageAggregateLEGACY> findByBusinessUuId(ImagesBusinessUuId businessUuId);

    // 2. PERSISTENCE
    void save(ImageAggregateLEGACY aggregate);

    // 3. SPECIFIC SEARCHES
    // Useful for deduplication or audit checks
    Optional<ImageAggregateLEGACY> findByUrl(ImageUrl imageUrl);

    // 4. COLLECTION QUERIES
    List<ImageAggregateLEGACY> findAllActive();

    // 5. LIFECYCLE
    void hardDelete(ImageUuId imagesUuId);
}

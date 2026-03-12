package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

import java.util.Optional;
import java.util.List;

public interface ProductRepository {

    // 1. IDENTITY & BUSINESS LOOKUP
    Optional<ProductAggregateRootLEGACY> findByUuId(ProductUuId productUuId);

    Optional<ProductAggregateRootLEGACY> findByBusinessUuId(ProductBusinessUuId businessUuId);

    // 2. PERSISTENCE
    void save(ProductAggregateRootLEGACY aggregate);

    // 3. REFERENCE LOOKUPS
    // Crucial for understanding which products are affected if a Gallery or PriceList changes
    List<ProductAggregateRootLEGACY> findAllByGalleryUuId(GalleryUuId galleryUuId);

    // 4. STATUS & CATEGORY QUERIES
    // Often used for the Admin Dashboard or storefront filtering
    List<ProductAggregateRootLEGACY> findAllByStatus(ProductStatus status);

    List<ProductAggregateRootLEGACY> findAllActive();

    // 5. LIFECYCLE
    void hardDelete(ProductUuId productUuId);
}

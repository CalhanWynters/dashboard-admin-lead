package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.product.dto;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductAggregate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Data Transfer Object for Product Aggregate.
 * Orchestrates IDs from sub-aggregates and flattens core product data.
 */
public record ProductDTO(
        UUID uuid,
        String businessUuid,
        String status,
        String region,
        int version,
        String thumbnailUrl,

        // Sub-Aggregate Identifiers
        UUID galleryUuid,
        UUID variantListUuid,
        UUID typeListUuid,
        UUID priceListUuid,

        // Metadata & Lifecycle
        boolean isArchived,
        boolean isSoftDeleted,
        Long optLockVer,
        OffsetDateTime lastSyncedAt
) {
    /**
     * Factory method to map from the Domain Aggregate to the DTO.
     */
    public static ProductDTO fromAggregate(ProductAggregate aggregate) {
        return new ProductDTO(
                // 1. Core Identity
                aggregate.getUuId().value().asUUID(),
                aggregate.getBusinessUuId().value().value(),
                aggregate.getProductStatus().value().name(),
                aggregate.getProductRegion().value().value(),
                aggregate.getProductVersion().value().value(),
                aggregate.getProductThumbnailUrl().value(),

                // 2. Sub-Aggregate Links (Unwrapping nested Record layers)
                aggregate.getGalleryUuId().value().asUUID(),
                aggregate.getVariantListUuId().value().asUUID(),
                aggregate.getTypeListUuId().value().asUUID(),
                aggregate.getPriceListUuId().value().asUUID(),

                // 3. Lifecycle & Versioning
                aggregate.getLifecycleState().archived(),
                aggregate.getLifecycleState().softDeleted(),
                aggregate.getOptLockVer(),
                aggregate.getLastSyncedAt()
        );
    }
}

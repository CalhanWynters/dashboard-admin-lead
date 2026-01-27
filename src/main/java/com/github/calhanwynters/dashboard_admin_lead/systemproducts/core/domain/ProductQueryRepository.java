package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * CQRS Read-Side Repository for Product Aggregates.
 * 2026 Strategy: Uses Audit timestamps for locking and Versions for schema evolution.
 */
public interface ProductQueryRepository {

    /**
     * Retrieves the full Aggregate Root.
     * Reconstitution will use the Version field to handle legacy data formats.
     */
    Optional<ProductAggregateRoot> findById(UuId productUuId);

    /**
     * Optimistic Locking: Returns the latest LastModified timestamp.
     * Command handlers compare this against their loaded snapshot to prevent lost updates.
     */
    OffsetDateTime getLatestTimestamp(UuId productUuId);

    /**
     * Checks for product existence by UuId.
     */
    boolean existsByUuId(UuId productUuId);

    /**
     * Returns all products for a specific business dashboard.
     */
    List<ProductAggregateRoot> findAllByBusinessId(UuId businessId);

    /**
     * Finds "Standard" products (those with a Type template).
     */
    List<ProductAggregateRoot> findByTypeTemplate(UuId typeColId);

    /**
     * Finds "Bespoke" products (those with unique physical attributes).
     */
    List<ProductAggregateRoot> findAllBespoke(UuId businessId);

    /**
     * Real-time counter for dashboard metrics.
     */
    long countByStatus(UuId businessId, String status);
}

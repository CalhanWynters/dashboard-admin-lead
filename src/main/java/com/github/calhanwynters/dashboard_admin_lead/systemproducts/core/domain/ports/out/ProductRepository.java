package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductAggregate;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

import java.util.Optional;
import java.util.List;

/**
 * Outbound Port for Product Management (2026 Edition).
 * Central Orchestrator for persisting Product compositions and their structural references.
 */
public interface ProductRepository {

    // --- 1. IDENTITY & BUSINESS LOOKUP ---

    /**
     * Primary lookup using the Domain Technical UUID.
     */
    Optional<ProductAggregate> findByUuId(ProductUuId productUuId);

    /**
     * Business lookup for external integrations and SKU-level uniqueness checks.
     */
    Optional<ProductAggregate> findByBusinessUuId(ProductBusinessUuId businessUuId);

    // --- 2. PERSISTENCE ---

    /**
     * Persists the Product state and returns the reconstituted aggregate.
     * Ensures the domain model reflects DB-synced metadata like optLockVer and schemaVer.
     */
    ProductAggregate save(ProductAggregate aggregate);

    // --- 3. REFERENCE LOOKUPS ---

    /**
     * Finds all products associated with a specific Gallery.
     * Crucial for dependency validation and SOC 2 impact analysis during media changes.
     */
    List<ProductAggregate> findAllByGalleryUuId(GalleryUuId galleryUuId);

    // --- 4. STATUS & CATEGORY QUERIES ---

    /**
     * Retrieves products filtered by their current lifecycle status (e.g., DRAFT, PUBLISHED).
     */
    List<ProductAggregate> findAllByStatus(ProductStatus status);

    /**
     * Retrieves all active products (non-archived and non-deleted).
     */
    List<ProductAggregate> findAllActive();

    // --- 5. LIFECYCLE ---

    /**
     * Permanent removal of the product record and its structural associations.
     */
    void hardDelete(ProductUuId productUuId);
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImageAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImagesBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUrl;

import java.util.Optional;
import java.util.List;

/**
 * Outbound Port for Image Management (2026 Edition).
 * Orchestrates the persistence of media assets for the refactored ImageAggregate.
 */
public interface ImagesRepository {

    // --- 1. IDENTITY & BUSINESS LOOKUP ---

    /**
     * Primary lookup using the Domain Technical UUID.
     */
    Optional<ImageAggregate> findByUuId(ImageUuId imageUuId);

    /**
     * Business lookup for external syncs and cross-domain references.
     */
    Optional<ImageAggregate> findByBusinessUuId(ImagesBusinessUuId businessUuId);

    // --- 2. PERSISTENCE ---

    /**
     * Persists the image state and returns the reconstituted aggregate.
     * Captures database-level metadata like optLockVer and lastSyncedAt.
     */
    ImageAggregate save(ImageAggregate aggregate);

    // --- 3. SPECIFIC SEARCHES ---

    /**
     * Finds an image by its unique URL.
     * Critical for deduplication and SOC 2 asset integrity checks.
     */
    Optional<ImageAggregate> findByUrl(ImageUrl imageUrl);

    // --- 4. COLLECTION QUERIES ---

    /**
     * Retrieves all active images (non-archived and non-deleted).
     */
    List<ImageAggregate> findAllActive();

    // --- 5. LIFECYCLE ---

    /**
     * Permanent removal of the image record and its metadata.
     */
    void hardDelete(ImageUuId imageUuId);
}

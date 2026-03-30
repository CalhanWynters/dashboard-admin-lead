package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryBusinessUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

import java.util.Optional;
import java.util.List;

/**
 * Outbound Port for Gallery Management (2026 Edition).
 * Orchestrates the persistence of media collections and their image memberships.
 */
public interface GalleryRepository {

    // --- 1. IDENTITY & BUSINESS LOOKUP ---

    /**
     * Primary technical lookup.
     */
    Optional<GalleryAggregate> findByUuId(GalleryUuId galleryUuId);

    /**
     * Lookup via external business reference.
     */
    Optional<GalleryAggregate> findByBusinessUuId(GalleryBusinessUuId businessUuId);

    // --- 2. RELATIONSHIP QUERIES ---

    /**
     * Reverse lookup to find all galleries containing a specific image.
     * Critical for SOC 2 cleanup and dependency validation before image deletion.
     */
    List<GalleryAggregate> findAllByImageUuId(ImageUuId imageUuId);

    // --- 3. PERSISTENCE ---

    /**
     * Atomic persistence of the Gallery state and its image memberships.
     * Returns the reconstituted aggregate to sync DB-side metadata (versions/timestamps).
     */
    GalleryAggregate save(GalleryAggregate aggregate);

    // --- 4. COLLECTION QUERIES ---

    /**
     * Returns all galleries currently in a 'PUBLIC' or published state.
     */
    List<GalleryAggregate> findAllPublic();

    /**
     * Returns all active galleries (non-archived and non-deleted).
     */
    List<GalleryAggregate> findAllActive();

    // --- 5. LIFECYCLE ---

    /**
     * Permanent removal of the gallery and its membership associations.
     */
    void hardDelete(GalleryUuId galleryUuId);
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryAggregate;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.*;

import java.util.Optional;
import java.util.List;

/**
 * Domain Repository for Gallery Aggregates.
 * Handles the persistence and retrieval of the Gallery state and its associated Image UUIDs.
 */
public interface GalleryRepository {

    /**
     * Reconstitutes a Gallery from the store using its internal Domain Identity.
     */
    Optional<GalleryAggregate> findByUuId(GalleryUuId uuId);

    /**
     * Reconstitutes a Gallery using its Business/External Identity.
     */
    Optional<GalleryAggregate> findByBusinessUuId(GalleryBusinessUuId businessUuId);

    /**
     * Retrieves all galleries.
     * In the Application Layer, this is used to list galleries before selecting one for an Action.
     */
    List<GalleryAggregate> findAll();

    /**
     * Persists the current state of the Gallery.
     * This includes publicity status, audit metadata, and the list of associated ImageUuIds.
     */
    void save(GalleryAggregate aggregate);

    /**
     * Removes the gallery from the store.
     */
    void delete(GalleryAggregate aggregate);

    /**
     * Domain Guard helper to ensure Business Identity uniqueness.
     */
    boolean existsByBusinessUuId(GalleryBusinessUuId businessUuId);
}

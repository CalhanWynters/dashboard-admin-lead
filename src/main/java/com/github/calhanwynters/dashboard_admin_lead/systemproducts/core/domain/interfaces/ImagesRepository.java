package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImageAggregate;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

import java.util.Optional;
import java.util.List;

/**
 * Domain Repository for Image Aggregates.
 * Manages the lifecycle and persistence of Image metadata and binary references.
 */
public interface ImagesRepository {

    /**
     * Reconstitutes an Image from persistence using the internal Domain UUID.
     */
    Optional<ImageAggregate> findByUuId(ImageUuId uuId);

    /**
     * Reconstitutes an Image using the Business/External UUID.
     */
    Optional<ImageAggregate> findByBusinessUuId(ImagesBusinessUuId businessUuId);

    /**
     * Retrieves all images.
     * Recommended: Implement pagination in the Infrastructure layer for this method.
     */
    List<ImageAggregate> findAll();

    /**
     * Persists the state of the ImageAggregate.
     * Implementation must use ImageFactory.reconstitute() when loading data.
     */
    void save(ImageAggregate aggregate);

    /**
     * Physically removes the image metadata from the store.
     * Note: Soft-deletion is handled via .save() after calling aggregate.softDelete().
     */
    void delete(ImageAggregate aggregate);

    /**
     * Existence check to prevent Business ID collisions.
     */
    boolean existsByBusinessUuId(ImagesBusinessUuId businessUuId);
}

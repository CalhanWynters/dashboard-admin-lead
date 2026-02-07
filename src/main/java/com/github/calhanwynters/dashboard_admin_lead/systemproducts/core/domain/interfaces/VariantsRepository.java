package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;

import java.util.Optional;
import java.util.List;

/**
 * Domain Repository for individual Variant Aggregates.
 * Manages variant metadata and their associated Feature assignments.
 */
public interface VariantsRepository {

    /**
     * Reconstitutes a Variant from the store via its internal Technical Identity.
     */
    Optional<VariantsAggregate> findByUuId(VariantsUuId uuId);

    /**
     * Reconstitutes a Variant via its Business/External Identity.
     */
    Optional<VariantsAggregate> findByBusinessUuId(VariantsBusinessUuId businessUuId);

    /**
     * Persists the variant state, including the assigned FeatureUuId set.
     * Implementation handles the 'deleted' flag mapping.
     */
    void save(VariantsAggregate aggregate);

    /**
     * Retrieves all variants currently in an active (non-deleted) state.
     */
    List<VariantsAggregate> findActive();

    /**
     * Physically removes the variant from the persistent store.
     */
    void delete(VariantsAggregate aggregate);

    /**
     * Checks if a Business UUID is already in use to prevent collisions.
     */
    boolean existsByBusinessUuId(VariantsBusinessUuId businessUuId);
}

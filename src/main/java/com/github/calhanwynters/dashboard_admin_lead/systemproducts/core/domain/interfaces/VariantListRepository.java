package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListAggregate;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;

import java.util.Optional;
import java.util.List;

/**
 * Domain Repository for Variant List Aggregates.
 * Handles the persistence of variant groupings and their membership state.
 */
public interface VariantListRepository {

    /**
     * Reconstitutes the VariantList from persistence via its internal UUID.
     */
    Optional<VariantListAggregate> findByUuId(VariantListUuId uuId);

    /**
     * Reconstitutes the VariantList via its Business/External Identity.
     */
    Optional<VariantListAggregate> findByBusinessUuId(VariantListBusinessUuId businessUuId);

    /**
     * Persists the current state of the VariantList, including the set of Variant references.
     * Maps the 'deleted' flag to the database lifecycle status.
     */
    void save(VariantListAggregate aggregate);

    /**
     * Retrieves all non-deleted Variant Lists.
     */
    List<VariantListAggregate> findActive();

    /**
     * Hard-deletes the VariantList from the store.
     */
    void delete(VariantListAggregate aggregate);

    /**
     * Validation helper to ensure Business UUID uniqueness.
     */
    boolean existsByBusinessUuId(VariantListBusinessUuId businessUuId);
}

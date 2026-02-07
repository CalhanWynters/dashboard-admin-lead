package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

import java.util.Optional;
import java.util.List;

/**
 * Domain Repository for Types Aggregates.
 * Manages individual product type definitions and their physical specifications.
 */
public interface TypesRepository {

    /**
     * Reconstitutes the Type via its internal technical Identity.
     */
    Optional<TypesAggregate> findByUuId(TypesUuId uuId);

    /**
     * Reconstitutes the Type via its Business/External Identity.
     */
    Optional<TypesAggregate> findByBusinessUuId(TypesBusinessUuId businessUuId);

    /**
     * Persists the current state of the Type.
     * Handles the mapping of TypesPhysicalSpecs and lifecycle (deleted) status.
     */
    void save(TypesAggregate aggregate);

    /**
     * Retrieves all non-deleted types.
     */
    List<TypesAggregate> findActive();

    /**
     * Physically removes the type record from the store.
     */
    void delete(TypesAggregate aggregate);

    /**
     * Prevents duplicate Business UUIDs during the creation flow.
     */
    boolean existsByBusinessUuId(TypesBusinessUuId businessUuId);
}

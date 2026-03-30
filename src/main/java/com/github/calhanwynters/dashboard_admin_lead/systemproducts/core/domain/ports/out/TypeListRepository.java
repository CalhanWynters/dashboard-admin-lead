package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListAggregate;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;

import java.util.Optional;
import java.util.List;

/**
 * Outbound Port for TypeList Management (2026 Edition).
 * Orchestrates the persistence of Type collections and their membership integrity.
 */
public interface TypeListRepository {

    // --- 1. IDENTITY & BUSINESS LOOKUP ---

    /**
     * Primary technical lookup using the Domain UUID.
     */
    Optional<TypeListAggregate> findByUuId(TypeListUuId typeListUuId);

    /**
     * Business lookup for external reference mapping and uniqueness checks.
     */
    Optional<TypeListAggregate> findByBusinessUuId(TypeListBusinessUuId businessUuId);

    // --- 2. PERSISTENCE ---

    /**
     * Atomically persists the TypeList state and its Set of Type memberships.
     * Returns the reconstituted aggregate to capture DB-synced metadata (versions/timestamps).
     */
    TypeListAggregate save(TypeListAggregate aggregate);

    // --- 3. REVERSE LOOKUP ---

    /**
     * Finds all lists that contain a specific Type.
     * Essential for SOC 2 dependency checks and preventing orphaned types during deletion.
     */
    List<TypeListAggregate> findAllByContainsType(TypesUuId typeUuId);

    // --- 4. COLLECTION QUERIES ---

    /**
     * Retrieves all active TypeLists (non-archived and non-deleted).
     */
    List<TypeListAggregate> findAllActive();

    // --- 5. LIFECYCLE ---

    /**
     * Permanent removal of the list and its membership associations.
     */
    void hardDelete(TypeListUuId typeListUuId);
}

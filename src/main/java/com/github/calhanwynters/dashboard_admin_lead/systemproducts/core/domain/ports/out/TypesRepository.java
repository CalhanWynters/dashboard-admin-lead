package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregate;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

import java.util.Optional;
import java.util.List;

/**
 * Outbound Port for Types Management (2026 Edition).
 * Orchestrates the persistence of product classifications and their physical specifications.
 */
public interface TypesRepository {

    // --- 1. IDENTITY & BUSINESS LOOKUP ---

    /**
     * Primary technical lookup using the Domain UUID.
     */
    Optional<TypesAggregate> findByUuId(TypesUuId typesUuId);

    /**
     * Business lookup for external reference mapping and uniqueness checks.
     */
    Optional<TypesAggregate> findByBusinessUuId(TypesBusinessUuId businessUuId);

    // --- 2. PERSISTENCE ---

    /**
     * Persists the Type state, including complex physical specifications.
     * Returns the reconstituted aggregate to capture DB-synced metadata (versions/timestamps).
     */
    TypesAggregate save(TypesAggregate aggregate);

    // --- 3. SEARCH & VALIDATION ---

    /**
     * Finds a Type by its specific name.
     * Essential for UI lookups and enforcing name uniqueness within the domain.
     */
    Optional<TypesAggregate> findByName(TypesName typesName);

    // --- 4. COLLECTION QUERIES ---

    /**
     * Retrieves all active Types (non-archived and non-deleted).
     */
    List<TypesAggregate> findAllActive();

    // --- 5. LIFECYCLE ---

    /**
     * Permanent removal of the Type record and its metadata.
     */
    void hardDelete(TypesAggregate aggregate);
}

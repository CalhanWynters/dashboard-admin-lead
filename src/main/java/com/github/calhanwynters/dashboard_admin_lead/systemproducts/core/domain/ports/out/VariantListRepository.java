package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListAggregate;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;

import java.util.Optional;
import java.util.List;

/**
 * Outbound Port for VariantList Management (2026 Edition).
 * Orchestrates the persistence of Variant collections and their membership integrity.
 */
public interface VariantListRepository {

    // --- 1. IDENTITY & BUSINESS LOOKUP ---

    /**
     * Primary technical lookup using the Domain UUID.
     */
    Optional<VariantListAggregate> findByUuId(VariantListUuId variantListUuId);

    /**
     * Business lookup for external reference mapping and uniqueness checks.
     */
    Optional<VariantListAggregate> findByBusinessUuId(VariantListBusinessUuId businessUuId);

    // --- 2. PERSISTENCE ---

    /**
     * Atomically persists the VariantList state and its Set of Variant memberships.
     * Returns the reconstituted aggregate to capture DB-synced metadata (versions/timestamps).
     */
    VariantListAggregate save(VariantListAggregate aggregate);

    // --- 3. REVERSE LOOKUP ---

    /**
     * Finds all lists that contain a specific Variant.
     * Essential for SOC 2 dependency checks and preventing orphaned variants during deletion.
     */
    List<VariantListAggregate> findAllByContainsVariant(VariantsUuId variantUuId);

    // --- 4. COLLECTION QUERIES ---

    /**
     * Retrieves all active VariantLists (non-archived and non-deleted).
     */
    List<VariantListAggregate> findAllActive();

    // --- 5. LIFECYCLE ---

    /**
     * Permanent removal of the list and its membership associations.
     */
    void hardDelete(VariantListUuId variantListUuId);
}

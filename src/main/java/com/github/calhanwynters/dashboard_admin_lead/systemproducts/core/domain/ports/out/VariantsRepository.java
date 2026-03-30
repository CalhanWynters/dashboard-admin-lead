package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsAggregate;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;

import java.util.Optional;
import java.util.List;

/**
 * Outbound Port for Variant Management (2026 Edition).
 * Orchestrates the persistence of individual product variants and their feature memberships.
 */
public interface VariantsRepository {

    // --- 1. IDENTITY & BUSINESS LOOKUP ---

    /**
     * Primary technical lookup using the Domain UUID.
     */
    Optional<VariantsAggregate> findByUuId(VariantsUuId variantsUuId);

    /**
     * Business lookup for external reference mapping and uniqueness checks.
     */
    Optional<VariantsAggregate> findByBusinessUuId(VariantsBusinessUuId businessUuId);

    // --- 2. PERSISTENCE ---

    /**
     * Persists the Variant state, including its name and assigned feature set.
     * Returns the reconstituted aggregate to capture DB-synced metadata (versions/timestamps).
     */
    VariantsAggregate save(VariantsAggregate aggregate);

    // --- 3. REVERSE LOOKUP ---

    /**
     * Identifies all variants impacted by a specific Feature.
     * Critical for SOC 2 dependency tracking and preventing orphaned features during deletion.
     */
    List<VariantsAggregate> findAllByFeatureUuId(FeatureUuId featureUuId);

    // --- 4. COLLECTION QUERIES ---

    /**
     * Retrieves all active Variants (non-archived and non-deleted).
     */
    List<VariantsAggregate> findAllActive();

    // --- 5. LIFECYCLE ---

    /**
     * Permanent removal of the Variant record and its feature associations.
     */
    void hardDelete(VariantsUuId variantsUuId);
}

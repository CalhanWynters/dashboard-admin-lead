package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ports.out;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PricingStrategyType;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

import java.util.Optional;
import java.util.List;

/**
 * Outbound Port for Price List Management (2026 Edition).
 * Orchestrates the persistence of complex pricing models and multi-currency mappings.
 */
public interface PriceListRepository {

    // --- 1. IDENTITY & VERSION ACCESS ---

    /**
     * Primary technical lookup using the Domain UUID.
     */
    Optional<PriceListAggregate> findByUuId(PriceListUuId priceListUuId);

    /**
     * Business lookup for external reference mapping and uniqueness checks.
     */
    Optional<PriceListAggregate> findByBusinessUuId(PriceListBusinessUuId businessUuId);

    // --- 2. PERSISTENCE ---

    /**
     * Atomically persists the PriceList state, including its nested pricing maps.
     * Returns the reconstituted aggregate to capture DB-level sync metadata and versioning.
     */
    PriceListAggregate save(PriceListAggregate aggregate);

    // --- 3. STRATEGY & STATE QUERIES ---

    /**
     * Finds all price lists operating under a specific pricing strategy.
     * Critical for SOC 2 boundary audits and financial consistency checks.
     */
    List<PriceListAggregate> findByStrategy(PricingStrategyType strategy);

    /**
     * Retrieves all currently active (non-archived/non-deleted) price lists.
     */
    List<PriceListAggregate> findAllActive();

    // --- 4. VERSION CONTROL ---

    /**
     * Reconstitutes a specific version of a price list for historical comparison or audit.
     */
    Optional<PriceListAggregate> findByBusinessUuIdAndVersion(
            PriceListBusinessUuId businessUuId,
            PriceListVersion version
    );

    // --- 5. LIFECYCLE ---

    /**
     * Permanent removal of the price list and all associated currency/purchase mappings.
     */
    void hardDelete(PriceListUuId priceListUuId);
}

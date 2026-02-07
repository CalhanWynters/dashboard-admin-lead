package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListAggregate;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

import java.util.Optional;
import java.util.List;

/**
 * Domain Repository for Price List Aggregates.
 * Manages complex versioned pricing matrices and strategy boundaries.
 */
public interface PriceListRepository {

    /**
     * Reconstitutes the PriceList using its internal Domain Identity.
     * Implementation must hydrate the multi-currency map and strategy boundary.
     */
    Optional<PriceListAggregate> findByUuId(PriceListUuId uuId);

    /**
     * Reconstitutes the PriceList using its Business/External Identity.
     */
    Optional<PriceListAggregate> findByBusinessUuId(PriceListBusinessUuId businessUuId);

    /**
     * Persists the entire state of the Price List.
     * Must ensure atomicity between the Aggregate state and its nested price entries.
     */
    void save(PriceListAggregate aggregate);

    /**
     * Finds the currently active Price List for a specific strategy type.
     * Useful for Application Services resolving the "current" price for a product.
     */
    List<PriceListAggregate> findActiveByStrategy(Class<?> strategyBoundary);

    /**
     * Removes the price list from the store.
     */
    void delete(PriceListAggregate aggregate);

    /**
     * Validation helper for Business ID uniqueness.
     */
    boolean existsByBusinessUuId(PriceListBusinessUuId businessUuId);
}

package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.pricelist.PriceListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.UuId;

import java.time.OffsetDateTime;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

/**
 * CQRS Read-Side Repository for PriceList Aggregates.
 * Specialized in resolving localized pricing snapshots in 2026.
 */
public interface PriceListQueryRepository {

    /**
     * Retrieves the full PriceList aggregate by its domain identity.
     * Reconstitutes the nested Map<UuId, Map<Currency, PurchasePricing>> structure.
     */
    Optional<PriceListAggregate> findById(UuId priceListUuId);

    /**
     * Optimistic Locking: Returns the latest LastModified timestamp for a specific PriceList.
     * Essential for 2026 Temporal Concurrency control.
     */
    OffsetDateTime getLatestTimestamp(UuId priceListUuId);

    /**
     * Finds all PriceLists belonging to a specific business.
     */
    List<PriceListAggregate> findAllByBusinessId(UuId businessId);

    /**
     * Finds a PriceList that contains a specific strategy boundary (e.g., Fixed vs. Tiered).
     */
    List<PriceListAggregate> findByStrategyBoundary(Class<?> strategyClass);

    /**
     * Specific 2026 E-commerce query: Finds all PriceLists that provide pricing
     * for a specific currency context (e.g., "Give me all USD pricelists").
     */
    List<PriceListAggregate> findAllByCurrency(Currency currency);

    /**
     * Existence check for a specific PriceList identity.
     */
    boolean existsByUuId(UuId priceListUuId);
}

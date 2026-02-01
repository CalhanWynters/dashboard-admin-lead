package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel.PurchasePricing;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles domain-driven state transitions for the PriceList Aggregate (2026 Edition).
 * Orchestrates multi-currency price updates while maintaining strict boundary invariants.
 */
public class PriceListBehavior {

    private final PriceListAggregate priceListAggregate;

    public PriceListBehavior(PriceListAggregate priceListAggregate) {
        DomainGuard.notNull(priceListAggregate, "PriceList instance");
        this.priceListAggregate = priceListAggregate;
    }

    /**
     * Injects or updates a specific currency price for a Target ID (Product/Type/Variant).
     */
    public PriceListAggregate addOrUpdatePrice(UuId targetId, Currency currency, PurchasePricing pricing) {
        // 1. Enforce the strategy boundary (e.g., must be PriceFixedPurchase)
        priceListAggregate.validateStrategyMatch(pricing);

        // 2. Immutability Pattern: Deep copy the nested maps to prevent aliasing
        Map<UuId, Map<Currency, PurchasePricing>> newOuterMap = new HashMap<>(priceListAggregate.getMultiCurrencyPrices());

        Map<Currency, PurchasePricing> newCurrencyMap = new HashMap<>(
                newOuterMap.getOrDefault(targetId, new HashMap<>())
        );

        newCurrencyMap.put(currency, pricing);
        newOuterMap.put(targetId, newCurrencyMap);

        // 3. Return a new state via direct instantiation to maintain Behavior-to-Root coupling
        return new PriceListAggregate(
                priceListAggregate.getPriceListId(),
                priceListAggregate.getPriceListUuId(),
                priceListAggregate.getBusinessId(),
                priceListAggregate.getStrategyBoundary(),
                priceListAggregate.getVersion().next(),  // Version Increment
                priceListAggregate.getAudit().update(),  // Temporal Audit Refresh
                newOuterMap
        );
    }

    /**
     * Removes a price for a specific currency context.
     * Logic: If the last currency for an item is removed, the entire Target ID entry is purged.
     */
    public PriceListAggregate removePrice(UuId targetId, Currency currency) {
        if (!priceListAggregate.getMultiCurrencyPrices().containsKey(targetId)) {
            return this.priceListAggregate;
        }

        Map<UuId, Map<Currency, PurchasePricing>> newOuterMap = new HashMap<>(priceListAggregate.getMultiCurrencyPrices());
        Map<Currency, PurchasePricing> newCurrencyMap = new HashMap<>(newOuterMap.get(targetId));

        newCurrencyMap.remove(currency);

        if (newCurrencyMap.isEmpty()) {
            newOuterMap.remove(targetId);
        } else {
            newOuterMap.put(targetId, newCurrencyMap);
        }

        return new PriceListAggregate(
                priceListAggregate.getPriceListId(),
                priceListAggregate.getPriceListUuId(),
                priceListAggregate.getBusinessId(),
                priceListAggregate.getStrategyBoundary(),
                priceListAggregate.getVersion().next(),
                priceListAggregate.getAudit().update(),
                newOuterMap
        );
    }
}

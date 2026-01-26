package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PurchasePricing;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.validationchecks.DomainGuard;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles domain-driven state transitions for the PriceList Aggregate (2026 Edition).
 * Orchestrates multi-currency price updates while maintaining strict boundary invariants.
 */
public class PriceListBehavior {

    private final PriceList priceList;

    public PriceListBehavior(PriceList priceList) {
        DomainGuard.notNull(priceList, "PriceList instance");
        this.priceList = priceList;
    }

    /**
     * Injects or updates a specific currency price for a Target ID (Product/Type/Variant).
     */
    public PriceList addOrUpdatePrice(UuId targetId, Currency currency, PurchasePricing pricing) {
        // 1. Enforce the strategy boundary (e.g., must be PriceFixedPurchase)
        priceList.validateStrategyMatch(pricing);

        // 2. Immutability Pattern: Deep copy the nested maps to prevent aliasing
        Map<UuId, Map<Currency, PurchasePricing>> newOuterMap = new HashMap<>(priceList.getMultiCurrencyPrices());

        Map<Currency, PurchasePricing> newCurrencyMap = new HashMap<>(
                newOuterMap.getOrDefault(targetId, new HashMap<>())
        );

        newCurrencyMap.put(currency, pricing);
        newOuterMap.put(targetId, newCurrencyMap);

        // 3. Return a new state via direct instantiation to maintain Behavior-to-Root coupling
        return new PriceList(
                priceList.getPriceListId(),
                priceList.getPriceListUuId(),
                priceList.getBusinessId(),
                priceList.getStrategyBoundary(),
                priceList.getVersion().next(),  // Version Increment
                priceList.getAudit().update(),  // Temporal Audit Refresh
                newOuterMap
        );
    }

    /**
     * Removes a price for a specific currency context.
     * Logic: If the last currency for an item is removed, the entire Target ID entry is purged.
     */
    public PriceList removePrice(UuId targetId, Currency currency) {
        if (!priceList.getMultiCurrencyPrices().containsKey(targetId)) {
            return this.priceList;
        }

        Map<UuId, Map<Currency, PurchasePricing>> newOuterMap = new HashMap<>(priceList.getMultiCurrencyPrices());
        Map<Currency, PurchasePricing> newCurrencyMap = new HashMap<>(newOuterMap.get(targetId));

        newCurrencyMap.remove(currency);

        if (newCurrencyMap.isEmpty()) {
            newOuterMap.remove(targetId);
        } else {
            newOuterMap.put(targetId, newCurrencyMap);
        }

        return new PriceList(
                priceList.getPriceListId(),
                priceList.getPriceListUuId(),
                priceList.getBusinessId(),
                priceList.getStrategyBoundary(),
                priceList.getVersion().next(),
                priceList.getAudit().update(),
                newOuterMap
        );
    }
}

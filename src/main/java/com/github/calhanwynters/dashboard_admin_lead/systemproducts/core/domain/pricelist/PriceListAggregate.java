package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.Version;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel.Money;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel.PurchasePricing;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.validationchecks.DomainGuard;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Currency;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Aggregate Root: PriceList (2026 Edition)
 * Manages localized currency mappings across the product catalog.
 */
public class PriceListAggregate {
    // Identity & Tenant Metadata
    private final PkId priceListId;
    private final UuId priceListUuId;
    private final UuId businessId;

    // Consistency Boundaries
    private final Class<? extends PurchasePricing> strategyBoundary;
    private final Version priceListVersion;
    private final AuditMetadata audit;

    /**
     * Internal Economic Mapping:
     * Key 1: Target Identity (Product, Type, or Variant UuId)
     * Key 2: Currency context
     */
    private final Map<UuId, Map<Currency, PurchasePricing>> multiCurrencyPrices;

    /**
     * Package-private constructor: Enforced by PriceListFactory.
     */
    PriceListAggregate(PkId priceListId, UuId priceListUuId, UuId businessId,
                       Class<? extends PurchasePricing> strategyBoundary,
                       Version priceListVersion, AuditMetadata audit,
                       Map<UuId, Map<Currency, PurchasePricing>> multiCurrencyPrices) {

        // Invariant Guarding
        DomainGuard.notNull(priceListUuId, "PriceList Identity");
        DomainGuard.notNull(businessId, "Business Identity");
        DomainGuard.notNull(strategyBoundary, "Strategy Boundary");
        DomainGuard.notNull(multiCurrencyPrices, "Price Mapping");

        this.priceListId = priceListId;
        this.priceListUuId = priceListUuId;
        this.businessId = businessId;
        this.strategyBoundary = strategyBoundary;
        this.priceListVersion = priceListVersion;
        this.audit = audit;

        // Validation of nested strategy consistency
        multiCurrencyPrices.values().forEach(currencyMap ->
                currencyMap.values().forEach(this::validateStrategyMatch)
        );

        this.multiCurrencyPrices = multiCurrencyPrices;
    }

    /**
     * Resolves a price from the map. Returns Optional.empty() if no mapping exists
     * for the target ID or the specific currency.
     */
    public Optional<Money> resolve(UuId targetId, Currency currency, BigDecimal quantity) {
        return Optional.ofNullable(multiCurrencyPrices.get(targetId))
                .map(currencyMap -> currencyMap.get(currency))
                .map(pricing -> pricing.calculate(quantity));
    }

    /**
     * Validates that a pricing strategy adheres to the aggregate's boundary lock.
     */
    void validateStrategyMatch(PurchasePricing pricing) {
        if (!strategyBoundary.isInstance(pricing)) {
            throw new IllegalStateException("Domain Violation: Price strategy mismatch. Expected: "
                    + strategyBoundary.getSimpleName());
        }
    }

    // ======================== Accessors ============================

    public PkId getPriceListId() { return priceListId; }
    public UuId getPriceListUuId() { return priceListUuId; }
    public UuId getBusinessId() { return businessId; }
    public Class<? extends PurchasePricing> getStrategyBoundary() { return strategyBoundary; }
    public Version getVersion() { return priceListVersion; }
    public AuditMetadata getAudit() { return audit; }
    public Map<UuId, Map<Currency, PurchasePricing>> getMultiCurrencyPrices() {
        return Collections.unmodifiableMap(multiCurrencyPrices);
    }

    public Set<Currency> getAvailableCurrencies(UuId targetId) {
        return Optional.ofNullable(multiCurrencyPrices.get(targetId))
                .map(Map::keySet)
                .orElse(Set.of());
    }

    public PriceListBehavior act() { return new PriceListBehavior(this); }
}

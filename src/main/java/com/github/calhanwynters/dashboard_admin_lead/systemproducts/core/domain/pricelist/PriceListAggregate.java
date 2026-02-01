package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.PriceListId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.PriceListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.PriceListBusinessUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.PriceListVersion;

import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel.Money;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel.PurchasePricing;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.math.BigDecimal;
import java.util.*;

/**
 * Aggregate Root: PriceList (2026 Edition)
 * Manages localized currency mappings across the product catalog.
 */
public class PriceListAggregate extends AbstractAggregateRoot<PriceListAggregate> {
    // Identity & Tenant Metadata
    PriceListId priceListId;
    PriceListUuId priceListUuId;
    PriceListBusinessUuId businessId;

    // Consistency Boundaries
    private final Class<? extends PurchasePricing> strategyBoundary;
    PriceListVersion priceListVersion;
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
    PriceListAggregate(PriceListId priceListId, PriceListUuId priceListUuId, PriceListBusinessUuId businessId,
                       Class<? extends PurchasePricing> strategyBoundary,
                       PriceListVersion priceListVersion, AuditMetadata audit,
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

    public PriceListId getPriceListId() { return priceListId; }
    public PriceListUuId getPriceListUuId() { return priceListUuId; }
    public PriceListBusinessUuId getBusinessId() { return businessId; }
    public Class<? extends PurchasePricing> getStrategyBoundary() { return strategyBoundary; }
    public PriceListVersion getVersion() { return priceListVersion; }
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

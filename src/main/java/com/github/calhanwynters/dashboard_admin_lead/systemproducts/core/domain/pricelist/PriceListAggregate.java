package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel.Money;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel.PurchasePricing;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * Aggregate Root for Price Management.
 * Manages multi-currency pricing strategies with atomic versioning and audit trails.
 */
public class PriceListAggregate extends BaseAggregateRoot<PriceListAggregate> {

    private final PriceListId priceListId;
    private final PriceListUuId priceListUuId;
    private final Class<? extends PurchasePricing> strategyBoundary;

    private PriceListVersion priceListVersion;
    private final Map<UuId, Map<Currency, PurchasePricing>> multiCurrencyPrices;

    public PriceListAggregate(PriceListId priceListId,
                              PriceListUuId priceListUuId,
                              PriceListBusinessUuId businessId,
                              Class<? extends PurchasePricing> strategyBoundary,
                              PriceListVersion priceListVersion,
                              AuditMetadata auditMetadata,
                              Map<UuId, Map<Currency, PurchasePricing>> multiCurrencyPrices) {

        super(auditMetadata);

        DomainGuard.notNull(priceListUuId, "PriceList Identity");
        DomainGuard.notNull(businessId, "Business Identity");
        DomainGuard.notNull(strategyBoundary, "Strategy Boundary");
        DomainGuard.notNull(multiCurrencyPrices, "Price Mapping");

        this.priceListId = priceListId;
        this.priceListUuId = priceListUuId;
        this.strategyBoundary = strategyBoundary;
        this.priceListVersion = priceListVersion;
        this.multiCurrencyPrices = new HashMap<>(multiCurrencyPrices);

        this.multiCurrencyPrices.values().forEach(currencyMap ->
                currencyMap.values().forEach(this::validateStrategyMatch)
        );
    }

    // --- DOMAIN ACTIONS ---

    /**
     * Injects or updates a specific currency price for a target.
     */
    public void addOrUpdatePrice(UuId targetId, Currency currency, PurchasePricing pricing, Actor actor) {
        DomainGuard.notNull(targetId, "Target Identity");
        DomainGuard.notNull(currency, "Currency");
        DomainGuard.notNull(pricing, "Pricing strategy");
        DomainGuard.notNull(actor, "Actor");

        validateStrategyMatch(pricing);

        this.multiCurrencyPrices
                .computeIfAbsent(targetId, k -> new HashMap<>())
                .put(currency, pricing);

        applyChangeMetadata(actor);
    }

    /**
     * Removes a price and purges empty target entries.
     */
    public void removePrice(UuId targetId, Currency currency, Actor actor) {
        DomainGuard.notNull(targetId, "Target Identity");
        DomainGuard.notNull(currency, "Currency");
        DomainGuard.notNull(actor, "Actor");

        Map<Currency, PurchasePricing> currencyMap = multiCurrencyPrices.get(targetId);
        if (currencyMap != null) {
            currencyMap.remove(currency);
            if (currencyMap.isEmpty()) {
                multiCurrencyPrices.remove(targetId);
            }
            applyChangeMetadata(actor);
        }
    }

    /**
     * Resolves a price based on quantity and currency.
     */
    public Optional<Money> resolve(UuId targetId, Currency currency, BigDecimal quantity) {
        return Optional.ofNullable(multiCurrencyPrices.get(targetId))
                .map(currencyMap -> currencyMap.get(currency))
                .map(pricing -> pricing.calculate(quantity));
    }

    // --- PRIVATE INVARIANTS & HELPERS ---

    private void validateStrategyMatch(PurchasePricing pricing) {
        if (!strategyBoundary.isInstance(pricing)) {
            throw new IllegalStateException("Domain Violation: Price strategy mismatch for " + strategyBoundary.getSimpleName());
        }
    }

    private void applyChangeMetadata(Actor actor) {
        this.priceListVersion = new PriceListVersion(this.priceListVersion.value().next());
        this.recordUpdate(actor);
    }

    // --- ACCESSORS ---
    public PriceListId getPriceListId() { return priceListId; }
    public PriceListUuId getPriceListUuId() { return priceListUuId; }
    public PriceListVersion getVersion() { return priceListVersion; }
    public Map<UuId, Map<Currency, PurchasePricing>> getMultiCurrencyPrices() {
        return Collections.unmodifiableMap(multiCurrencyPrices);
    }
}

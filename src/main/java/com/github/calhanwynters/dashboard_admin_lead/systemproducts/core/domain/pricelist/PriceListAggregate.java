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

public class PriceListAggregate extends BaseAggregateRoot<PriceListAggregate> {

    private final PriceListId priceListId;
    private final PriceListUuId priceListUuId;
    private final PriceListBusinessUuId businessId;
    private final Class<? extends PurchasePricing> strategyBoundary;
    private PriceListVersion priceListVersion;
    private final Map<UuId, Map<Currency, PurchasePricing>> multiCurrencyPrices;

    PriceListAggregate(PriceListId priceListId, PriceListUuId priceListUuId, PriceListBusinessUuId businessId,
                       Class<? extends PurchasePricing> strategyBoundary,
                       PriceListVersion priceListVersion, AuditMetadata auditMetadata,
                       Map<UuId, Map<Currency, PurchasePricing>> multiCurrencyPrices) {

        super(auditMetadata);

        DomainGuard.notNull(priceListUuId, "PriceList Identity");
        DomainGuard.notNull(businessId, "Business Identity");
        DomainGuard.notNull(strategyBoundary, "Strategy Boundary");
        DomainGuard.notNull(multiCurrencyPrices, "Price Mapping");

        this.priceListId = priceListId;
        this.priceListUuId = priceListUuId;
        this.businessId = businessId;
        this.strategyBoundary = strategyBoundary;
        this.priceListVersion = priceListVersion;
        // Ensure the internal map is mutable to support Behavior-driven updates
        this.multiCurrencyPrices = new HashMap<>(multiCurrencyPrices);

        multiCurrencyPrices.values().forEach(currencyMap ->
                currencyMap.values().forEach(this::validateStrategyMatch)
        );
    }

    public Optional<Money> resolve(UuId targetId, Currency currency, BigDecimal quantity) {
        return Optional.ofNullable(multiCurrencyPrices.get(targetId))
                .map(currencyMap -> currencyMap.get(currency))
                .map(pricing -> pricing.calculate(quantity));
    }

    void validateStrategyMatch(PurchasePricing pricing) {
        if (!strategyBoundary.isInstance(pricing)) {
            throw new IllegalStateException("Domain Violation: Price strategy mismatch.");
        }
    }

    // Package-private internal update for Behavior class
    void incrementVersion() {
        this.priceListVersion = new PriceListVersion(this.priceListVersion.value().next());
    }

    /**
     * Bridge method to allow the package-neighbor Behavior class
     * to trigger a business audit update.
     */
    void triggerAuditUpdate(Actor actor) {
        this.recordUpdate(actor);
    }

    void updatePricesInternal(Map<UuId, Map<Currency, PurchasePricing>> newPrices) {
        this.multiCurrencyPrices.clear();
        this.multiCurrencyPrices.putAll(newPrices);
    }


    // Accessors
    public PriceListId getPriceListId() { return priceListId; }
    public PriceListUuId getPriceListUuId() { return priceListUuId; }
    public PriceListBusinessUuId getBusinessId() { return businessId; }
    public Class<? extends PurchasePricing> getStrategyBoundary() { return strategyBoundary; }
    public PriceListVersion getVersion() { return priceListVersion; }
    public Map<UuId, Map<Currency, PurchasePricing>> getMultiCurrencyPrices() {
        return Collections.unmodifiableMap(multiCurrencyPrices);
    }

    public PriceListBehavior act() { return new PriceListBehavior(this); }


    public void updatePrice(UuId targetId, Currency currency, PurchasePricing pricing, Actor actor) {
        DomainGuard.notNull(pricing, "Pricing strategy");
        DomainGuard.notNull(actor, "Actor");
        validateStrategyMatch(pricing);

        // Update internal state
        Map<Currency, PurchasePricing> currencyMap = multiCurrencyPrices.computeIfAbsent(targetId, k -> new HashMap<>());
        currencyMap.put(currency, pricing);

        // Atomic version bump and audit
        this.priceListVersion = new PriceListVersion(this.priceListVersion.value().next());
        this.recordUpdate(actor);
    }

    public void removePrice(UuId targetId, Currency currency, Actor actor) {
        DomainGuard.notNull(actor, "Actor");

        if (multiCurrencyPrices.containsKey(targetId)) {
            Map<Currency, PurchasePricing> currencyMap = multiCurrencyPrices.get(targetId);
            currencyMap.remove(currency);

            if (currencyMap.isEmpty()) {
                multiCurrencyPrices.remove(targetId);
            }

            this.priceListVersion = new PriceListVersion(this.priceListVersion.value().next());
            this.recordUpdate(actor);
        }
    }

}

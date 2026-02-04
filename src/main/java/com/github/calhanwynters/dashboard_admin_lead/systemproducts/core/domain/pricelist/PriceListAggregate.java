package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel.PurchasePricing;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.*;

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
        this.registerEvent(new PriceUpdatedEvent(this.priceListUuId, targetId, currency, pricing, this.priceListVersion, actor));
    }

    public void removePrice(UuId targetId, Currency currency, Actor actor) {
        DomainGuard.notNull(targetId, "Target Identity");
        DomainGuard.notNull(currency, "Currency");
        DomainGuard.notNull(actor, "Actor");

        Map<Currency, PurchasePricing> currencyMap = multiCurrencyPrices.get(targetId);
        if (currencyMap != null && currencyMap.containsKey(currency)) {
            currencyMap.remove(currency);
            if (currencyMap.isEmpty()) {
                multiCurrencyPrices.remove(targetId);
            }
            applyChangeMetadata(actor);
            this.registerEvent(new PriceRemovedEvent(this.priceListUuId, targetId, currency, this.priceListVersion, actor));
        }
    }

    public void softDelete(Actor actor) {
        DomainGuard.notNull(actor, "Actor");
        this.recordUpdate(actor);
        this.registerEvent(new PriceListSoftDeletedEvent(this.priceListUuId, actor));
    }

    public void hardDelete(Actor actor) {
        DomainGuard.notNull(actor, "Actor");
        this.registerEvent(new PriceListHardDeletedEvent(this.priceListUuId, actor));
    }

    /**
     * Purges all currency pricing for a specific target in a single atomic action.
     */
    public void purgeTargetPricing(UuId targetId, Actor actor) {
        DomainGuard.notNull(targetId, "Target Identity");
        DomainGuard.notNull(actor, "Actor");

        if (this.multiCurrencyPrices.containsKey(targetId)) {
            this.multiCurrencyPrices.remove(targetId);
            applyChangeMetadata(actor);
            this.registerEvent(new TargetPricingPurgedEvent(this.priceListUuId, targetId, this.priceListVersion, actor));
        }
    }

    /*
       Note: strategyBoundary is currently 'final' in your root.
       If the business requires shifting strategies, you would remove 'final'
       and implement this method:
    */
    public void shiftStrategy(Class<? extends PurchasePricing> newStrategy, Actor actor) {
        DomainGuard.notNull(newStrategy, "New Strategy");
        DomainGuard.notNull(actor, "Actor");

        String oldName = this.strategyBoundary.getSimpleName();
        // this.strategyBoundary = newStrategy; // Requires removing 'final'

        this.recordUpdate(actor);
        this.registerEvent(new PriceListStrategyChangedEvent(this.priceListUuId, oldName, newStrategy.getSimpleName(), actor));
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

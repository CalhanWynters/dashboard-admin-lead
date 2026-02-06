package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.events.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.events.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PurchasePricing;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

import java.util.*;
import java.util.stream.Collectors;

public class PriceListAggregate extends BaseAggregateRoot<PriceListAggregate> {

    private final PriceListId priceListId;
    private final PriceListUuId priceListUuId;
    private final Class<? extends PurchasePricing> strategyBoundary;

    private PriceListVersion priceListVersion;
    private boolean isActive;
    private final Map<UuId, Map<Currency, PurchasePricing>> multiCurrencyPrices;

    public PriceListAggregate(PriceListId priceListId,
                              PriceListUuId priceListUuId,
                              Class<? extends PurchasePricing> strategyBoundary,
                              PriceListVersion priceListVersion,
                              boolean isActive,
                              AuditMetadata auditMetadata,
                              Map<UuId, Map<Currency, PurchasePricing>> multiCurrencyPrices) {

        super(auditMetadata);
        this.priceListId = priceListId;
        this.priceListUuId = DomainGuard.notNull(priceListUuId, "PriceList Identity");
        this.strategyBoundary = DomainGuard.notNull(strategyBoundary, "Strategy Boundary");
        this.priceListVersion = DomainGuard.notNull(priceListVersion, "Version");
        this.isActive = isActive;
        this.multiCurrencyPrices = new HashMap<>(multiCurrencyPrices);
    }


    public void activate(Actor actor) {
        PriceListBehavior.ensureActivationTransition(this.isActive, true);

        this.applyChange(actor,
                new PriceListActivatedEvent(this.priceListUuId, actor),
                () -> this.isActive = true // Explicitly set to true
        );
    }

    public void deactivate(Actor actor) {
        PriceListBehavior.ensureActivationTransition(this.isActive, false);

        this.applyChange(actor,
                new PriceListDeactivatedEvent(this.priceListUuId, actor),
                () -> this.isActive = false // Explicitly set to false
        );
    }

    public void addOrUpdatePrice(UuId targetId, Currency currency, PurchasePricing pricing, Actor actor) {
        PriceListBehavior.ensureActive(this.isActive);
        PriceListBehavior.validateStrategyMatch(this.strategyBoundary, pricing);

        // 1. Capture current state for trend analysis
        var currentPrice = Optional.ofNullable(multiCurrencyPrices.get(targetId))
                .map(m -> m.get(currency));

        var nextVersion = PriceListBehavior.evaluateVersionIncrement(this.priceListVersion);

        this.applyChange(actor,
                new PriceUpdatedEvent(this.priceListUuId, targetId, currency, pricing, nextVersion, actor),
                () -> {
                    this.priceListVersion = nextVersion;
                    this.multiCurrencyPrices.computeIfAbsent(targetId, k -> new HashMap<>()).put(currency, pricing);

                    // 2. Fire specialized trend events (Safe Comparison)
                    currentPrice.ifPresent(old -> {
                        // Trend events are only relevant when comparing the SAME currency
                        if (pricing.isMoreExpensiveThan(old)) {
                            this.registerEvent(new PriceIncreaseEvent(this.priceListUuId, targetId, currency, actor));
                        } else if (old.isMoreExpensiveThan(pricing)) {
                            // Utilizing the missing event you identified earlier
                            this.registerEvent(new PriceDecreaseEvent(this.priceListUuId, targetId, currency, actor));
                        }
                    });
                }
        );
    }

    public void shiftStrategy(Class<? extends PurchasePricing> newStrategy, Actor actor) {
        PriceListBehavior.ensureActive(this.isActive);
        String oldName = this.strategyBoundary.getSimpleName();

        this.applyChange(actor,
                new PriceListStrategyChangedEvent(this.priceListUuId, oldName, newStrategy.getSimpleName(), actor),
                null // If strategyBoundary is final, this just audits the attempt/intent
        );
    }

    private void applyVersionIncrementEvent() {
        this.registerEvent(new PriceListVersionIncrementedEvent(this.priceListUuId, this.priceListVersion));
    }

    public void recordStrategyViolation(String attemptedStrategy, Actor actor) {
        this.applyChange(actor, new PriceStrategyViolationAttemptedEvent(this.priceListUuId, attemptedStrategy, actor), null);
    }


    public static PriceListAggregate create(PriceListUuId uuId,
                                            PriceListBusinessUuId businessId,
                                            Class<? extends PurchasePricing> boundary,
                                            Actor creator) {

        PriceListAggregate aggregate = new PriceListAggregate(
                null,
                uuId,
                boundary,
                PriceListVersion.INITIAL,
                false,
                AuditMetadata.create(creator),
                new HashMap<>()
        );

        // This works here because it's inside the class that extends AbstractAggregateRoot
        aggregate.registerEvent(new PriceListCreatedEvent(uuId, businessId, creator));

        return aggregate;
    }

    public void removePrice(UuId targetId, Currency currency, Actor actor) {
        PriceListBehavior.ensureActive(this.isActive);
        PriceListBehavior.ensureTargetExists(this.multiCurrencyPrices, targetId, currency);
        var nextVersion = PriceListBehavior.evaluateVersionIncrement(this.priceListVersion);

        this.applyChange(actor,
                new PriceRemovedEvent(this.priceListUuId, targetId, currency, nextVersion, actor),
                () -> {
                    this.priceListVersion = nextVersion;
                    Map<Currency, PurchasePricing> currencyMap = multiCurrencyPrices.get(targetId);
                    currencyMap.remove(currency);
                    if (currencyMap.isEmpty()) multiCurrencyPrices.remove(targetId);
                }
        );
    }

    public void softDelete(Actor actor) {
        this.applyChange(actor, new PriceListSoftDeletedEvent(this.priceListUuId, actor), null);
    }

    public void hardDelete(Actor actor) {
        this.applyChange(actor, new PriceListHardDeletedEvent(this.priceListUuId, actor), null);
    }

    /**
     * Applies a percentage-based adjustment to ALL prices in the list.
     * @param percentage Positive for increase (e.g., 5.0), negative for decrease (e.g., -3.5).
     */
    public void applyBulkAdjustment(String reason, double percentage, Actor actor) {
        PriceListBehavior.ensureActive(this.isActive);
        PriceListBehavior.validateBulkAdjustment(percentage);

        var nextVersion = PriceListBehavior.evaluateVersionIncrement(this.priceListVersion);
        double factor = 1 + (percentage / 100.0);

        this.applyChange(actor,
                new BulkPriceAdjustmentEvent(this.priceListUuId, reason, percentage, actor),
                () -> {
                    this.priceListVersion = nextVersion;

                    // Perform the bulk mutation across all targets and currencies
                    this.multiCurrencyPrices.values().forEach(currencyMap ->
                            currencyMap.replaceAll((currency, pricing) ->
                                    pricing.adjustedBy(factor) // Assuming PurchasePricing has this method
                            )
                    );
                }
        );
    }

    // Accessors
    public PriceListId getPriceListId() { return priceListId; }
    public boolean isActive() { return isActive; }

    /**
     * Accessor providing a deep unmodifiable view of the price matrix.
     * This prevents the "leaky abstraction" where a caller could
     * multiCurrencyPrices.get(id).put(currency, illegalPricing).
     */
    public Map<UuId, Map<Currency, PurchasePricing>> getMultiCurrencyPrices() {
        return multiCurrencyPrices.entrySet().stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                e -> Map.copyOf(e.getValue())
                        ),
                        Collections::unmodifiableMap
                ));
    }
}

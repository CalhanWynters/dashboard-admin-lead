package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesBehavior;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.events.FeatureBusinessUuIdChangedEvent;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.events.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PurchasePricing;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

/**
 * Aggregate Root for Price Lists.
 * Optimized for SOC 2 Processing Integrity and Role-Based Access Control.
 */
public class PriceListAggregate extends BaseAggregateRoot<PriceListAggregate> {

    private final PriceListId priceListId;
    private final PriceListUuId priceListUuId;
    private final Class<? extends PurchasePricing> strategyBoundary;

    private PriceListBusinessUuId priceListBusinessUuId;
    private PriceListVersion priceListVersion;
    private ProductBooleans productBooleans;
    private boolean isActive;
    private final Map<UuId, Map<Currency, PurchasePricing>> multiCurrencyPrices;

    public PriceListAggregate(PriceListId priceListId,
                              PriceListUuId priceListUuId,
                              Class<? extends PurchasePricing> strategyBoundary,
                              PriceListBusinessUuId priceListBusinessUuId,
                              PriceListVersion priceListVersion,
                              boolean isActive,
                              ProductBooleans productBooleans, // Added param
                              AuditMetadata auditMetadata,
                              Map<UuId, Map<Currency, PurchasePricing>> multiCurrencyPrices) {

        super(auditMetadata);
        this.priceListId = priceListId;
        this.priceListUuId = DomainGuard.notNull(priceListUuId, "PriceList Identity");
        this.strategyBoundary = DomainGuard.notNull(strategyBoundary, "Strategy Boundary");
        this.priceListBusinessUuId = DomainGuard.notNull(priceListBusinessUuId, "PriceList Business UUID");
        this.priceListVersion = DomainGuard.notNull(priceListVersion, "Version");
        this.isActive = isActive;
        this.productBooleans = productBooleans != null ? productBooleans : new ProductBooleans(false, false);
        this.multiCurrencyPrices = new HashMap<>(multiCurrencyPrices);
    }

    // --- DOMAIN ACTIONS ---

    // Is the "Create" function needed?
    // Fix various methods here. This file wrongfully uses version as optimistic lock.
    // Need a 2-liner pattern method for PriceListUpdateVersionCommand

    public void updateBusinessUuId(PriceListBusinessUuId newId, Actor actor) {
        PriceListBehavior.ensureActive(this.productBooleans.softDeleted());

        // Validate using your existing logic (Admin-only, non-null, difference check)
        var validatedId = PriceListBehavior.evaluateBusinessIdChange(this.priceListBusinessUuId, newId, actor);

        this.applyChange(actor,
                new PriceListBusinessUuIdChangedEvent(priceListUuId, this.priceListBusinessUuId, validatedId, actor),
                () -> this.priceListBusinessUuId = validatedId);
    }

    public void syncToKafka(Actor actor) {
        PriceListBehavior.ensureActive(this.productBooleans.softDeleted());
        PriceListBehavior.verifySyncAuthority(actor);

        this.applyChange(actor,
                new PriceListDataSyncedEvent(priceListUuId, priceListBusinessUuId, strategyBoundary, priceListVersion, isActive, productBooleans, multiCurrencyPrices, actor),
                null);
    }

    public void activate(Actor actor) {
        // Line 1: Logic & Auth
        PriceListBehavior.verifyPriceModificationAuthority(actor);
        PriceListBehavior.ensureActivationTransition(this.isActive, true);

        // Line 2: Execution
        this.applyChange(actor,
                new PriceListActivatedEvent(this.priceListUuId, actor),
                () -> this.isActive = true
        );
    }

    public void deactivate(Actor actor) {
        PriceListBehavior.verifyPriceModificationAuthority(actor);
        PriceListBehavior.ensureActivationTransition(this.isActive, false);

        this.applyChange(actor,
                new PriceListDeactivatedEvent(this.priceListUuId, actor),
                () -> this.isActive = false
        );
    }

    public void addOrUpdatePrice(UuId targetId, Currency currency, PurchasePricing pricing, Actor actor) {
        // Lifecycle Guard (Hard Lock)
        PriceListBehavior.ensureLifecycleActive(this.productBooleans.softDeleted());
        // Operational Guard (Soft Lock)
        PriceListBehavior.ensureOperationalActive(this.isActive);

        PriceListBehavior.verifyPriceModificationAuthority(actor);
        PriceListBehavior.validateStrategyMatch(this.strategyBoundary, pricing);

        var currentPrice = Optional.ofNullable(multiCurrencyPrices.get(targetId)).map(m -> m.get(currency));
        var nextVersion = PriceListBehavior.evaluateVersionIncrement(this.priceListVersion);


        this.applyChange(actor,
                new PriceUpdatedEvent(this.priceListUuId, targetId, currency, pricing, nextVersion, actor),
                () -> {
                    this.priceListVersion = nextVersion;
                    this.multiCurrencyPrices.computeIfAbsent(targetId, k -> new HashMap<>()).put(currency, pricing);

                    currentPrice.ifPresent(old -> {
                        if (pricing.isMoreExpensiveThan(old)) {
                            this.registerEvent(new PriceIncreaseEvent(this.priceListUuId, targetId, currency, actor));
                        } else if (old.isMoreExpensiveThan(pricing)) {
                            this.registerEvent(new PriceDecreaseEvent(this.priceListUuId, targetId, currency, actor));
                        }
                    });
                }
        );
    }

    public void applyBulkAdjustment(String reason, double percentage, Actor actor) {
        // Line 1: High-Risk Auth (SEC-001)
        PriceListBehavior.verifyBulkAdjustmentAuthority(actor);
        PriceListBehavior.ensureActive(this.isActive);
        PriceListBehavior.validateBulkAdjustment(percentage);

        var nextVersion = PriceListBehavior.evaluateVersionIncrement(this.priceListVersion);
        double factor = 1 + (percentage / 100.0);

        this.applyChange(actor,
                new BulkPriceAdjustmentEvent(this.priceListUuId, reason, percentage, actor),
                () -> {
                    this.priceListVersion = nextVersion;
                    this.multiCurrencyPrices.values().forEach(currencyMap ->
                            currencyMap.replaceAll((currency, pricing) -> pricing.adjustedBy(factor))
                    );
                }
        );
    }

    public void shiftStrategy(Class<? extends PurchasePricing> newStrategy, Actor actor) {
        PriceListBehavior.verifyBulkAdjustmentAuthority(actor); // Admin level
        PriceListBehavior.ensureActive(this.isActive);
        String oldName = this.strategyBoundary.getSimpleName();

        this.applyChange(actor,
                new PriceListStrategyChangedEvent(this.priceListUuId, oldName, newStrategy.getSimpleName(), actor),
                null
        );
    }

    public void removePrice(UuId targetId, Currency currency, Actor actor) {
        PriceListBehavior.verifyPriceModificationAuthority(actor);
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

    public void recordStrategyViolation(String attemptedStrategy, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.equals(Actor.SYSTEM)) {
            throw new DomainAuthorizationException("Unauthorized violation recording.", "SEC-403", actor);
        }

        this.applyChange(actor,
                new PriceStrategyViolationAttemptedEvent(this.priceListUuId, attemptedStrategy, actor),
                null
        );
    }

    public void softDelete(Actor actor) {
        PriceListBehavior.ensureLifecycleActive(this.productBooleans.softDeleted());
        PriceListBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new PriceListSoftDeletedEvent(this.priceListUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), true)
        );
    }

    public void hardDelete(Actor actor) {
        PriceListBehavior.verifyHardDeleteAuthority(actor);
        this.applyChange(actor, new PriceListHardDeletedEvent(this.priceListUuId, actor), null);
    }

    public void restore(Actor actor) {
        if (!this.productBooleans.softDeleted()) return;
        PriceListBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new PriceListRestoredEvent(this.priceListUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), false)
        );
    }

    public void archive(Actor actor) {
        PriceListBehavior.verifyLifecycleAuthority(actor);
        this.applyChange(actor,
                new PriceListArchivedEvent(this.priceListUuId, actor),
                () -> this.productBooleans = new ProductBooleans(true, this.productBooleans.softDeleted())
        );
    }

    public void unarchive(Actor actor) {
        // Line 1: Auth check (matches archive authority)
        PriceListBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record with archived = false)
        this.applyChange(actor,
                new PriceListUnarchivedEvent(this.priceListUuId, actor),
                () -> this.productBooleans = new ProductBooleans(false, this.productBooleans.softDeleted())
        );
    }


    // --- ACCESSORS ---

    public PriceListId getPriceListId() { return priceListId; }
    public PriceListUuId getPriceListUuId() { return priceListUuId; }
    public PriceListBusinessUuId getPriceListBusinessUuId() { return priceListBusinessUuId; }
    public boolean isActive() { return isActive; }
    public PriceListVersion getPriceListVersion() { return priceListVersion; }

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

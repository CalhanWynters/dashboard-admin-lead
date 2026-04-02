package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.events.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.PriceListEntity;

import java.time.OffsetDateTime;
import java.util.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

/**
 * Modernized PriceList Aggregate.
 * Optimized for SOC 2 Processing Integrity and Multi-Currency State Management.
 */
public class PriceListAggregate extends BaseAggregateRoot<PriceListAggregate, PriceListId, PriceListUuId, PriceListBusinessUuId> {

    private final PricingStrategyType strategyBoundary;
    private PriceListVersion priceListVersion;
    private boolean isActive; // Operational status
    private final Map<UuId, Map<Currency, PurchasePricing>> multiCurrencyPrices;

    public PriceListAggregate(PriceListId id, PriceListUuId uuId, PriceListBusinessUuId businessUuId,
                              PricingStrategyType strategyBoundary, PriceListVersion version,
                              boolean isActive, Map<UuId, Map<Currency, PurchasePricing>> prices,
                              AuditMetadata auditMetadata, LifecycleState lifecycleState,
                              Long optLockVer, Integer schemaVer, OffsetDateTime lastSyncedAt) {
        super(id, uuId, businessUuId, auditMetadata, optLockVer, schemaVer, lastSyncedAt);
        this.strategyBoundary = strategyBoundary;
        this.priceListVersion = version;
        this.isActive = isActive;
        this.multiCurrencyPrices = new HashMap<>(prices != null ? prices : Collections.emptyMap());
        this.lifecycleState = lifecycleState;
    }

    public static PriceListAggregate create(PriceListUuId uuId, PriceListBusinessUuId bUuId,
                                            PricingStrategyType strategy, Actor actor) {

        // 1. Centralized Validation
        return PriceListFactory.create(uuId, bUuId, strategy, actor);
    }

    // --- DOMAIN ACTIONS ---

    public void incrementVersion(Actor actor) {
        this.applyDomainChange(actor, this.priceListVersion,
                (curr, auth) -> {
                    // CHANGE: Use the base method instead of the deleted Behavior method
                    BaseAggregateRoot.verifySyncAuthority(auth);
                    return PriceListBehavior.incrementVersion(curr);
                },
                next -> new PriceListVersionIncrementedEvent(this.uuId, next, actor),
                next -> this.priceListVersion = next
        );
    }


    public void updateBusinessUuId(PriceListBusinessUuId newId, Actor actor) {
        this.executeBusinessUuIdUpdate(newId, actor,
                val -> new PriceListBusinessUuIdChangedEvent(this.uuId, this.businessUuId, val, actor)
        );
    }

    public void toggleActivation(boolean targetStatus, Actor actor) {
        this.applyDomainChange(actor, targetStatus,
                (next, auth) -> {
                    // CHANGE: Use the base method
                    BaseAggregateRoot.verifyLifecycleAuthority(auth);
                    PriceListBehavior.ensureActivationTransition(this.isActive, next);
                    return next;
                },
                next -> next ? new PriceListActivatedEvent(this.uuId, actor) : new PriceListDeactivatedEvent(this.uuId, actor),
                next -> this.isActive = next
        );
    }


    public void addOrUpdatePrice(UuId targetId, Currency currency, PurchasePricing pricing, Actor actor) {
        // 1. Mandatory Guards
        ensureActive(); // Lifecycle check (Base)
        PriceListBehavior.ensureOperationalActive(this.isActive); // Business check (Behavior)
        PriceListBehavior.verifyPriceModificationAuthority(actor);
        PriceListBehavior.validateStrategyMatch(this.strategyBoundary, pricing);

        // 2. Determine Price Shift (for secondary events)
        var currentPrice = Optional.ofNullable(multiCurrencyPrices.get(targetId)).map(m -> m.get(currency));

        // 3. Orchestration
        this.applyChange(actor,
                new PriceUpdatedEvent(this.uuId, targetId, currency, pricing, actor),
                () -> {
                    this.multiCurrencyPrices.computeIfAbsent(targetId, k -> new HashMap<>()).put(currency, pricing);
                    this.priceListVersion = PriceListBehavior.incrementVersion(this.priceListVersion);

                    currentPrice.ifPresent(old -> {
                        if (pricing.isMoreExpensiveThan(old)) {
                            this.registerEvent(new PriceIncreaseEvent(this.uuId, targetId, currency, actor));
                        } else if (old.isMoreExpensiveThan(pricing)) {
                            this.registerEvent(new PriceDecreaseEvent(this.uuId, targetId, currency, actor));
                        }
                    });
                }
        );
    }

    public void removePrice(UuId targetId, Currency currency, Actor actor) {
        // 1. Mandatory Guards
        ensureActive();
        PriceListBehavior.ensureOperationalActive(this.isActive);

        // NEW: Use the specific behavior guard
        PriceListBehavior.verifyPriceRemovalAuthority(actor);

        // 2. Orchestration
        this.applyChange(actor,
                new PriceRemovedEvent(this.uuId, targetId, currency, actor),
                () -> {
                    Optional.ofNullable(this.multiCurrencyPrices.get(targetId))
                            .ifPresent(m -> {
                                m.remove(currency);
                                if (m.isEmpty()) {
                                    this.multiCurrencyPrices.remove(targetId);
                                }
                            });

                    this.priceListVersion = PriceListBehavior.incrementVersion(this.priceListVersion);
                }
        );
    }

    public void applyBulkAdjustment(String reason, double percentage, Actor actor) {
        ensureActive();
        PriceListBehavior.verifyBulkAdjustmentAuthority(actor);
        PriceListBehavior.validateBulkAdjustment(percentage);

        double factor = 1 + (percentage / 100.0);

        this.applyChange(actor,
                new BulkPriceAdjustmentEvent(this.uuId, reason, percentage, actor),
                () -> {
                    this.multiCurrencyPrices.values().forEach(m -> m.replaceAll((c, p) -> p.adjustedBy(factor)));
                    this.priceListVersion = PriceListBehavior.incrementVersion(this.priceListVersion);
                }
        );
    }

    public void syncToKafka(Actor actor) {
        this.executeSync(actor,
                auth -> new PriceListDataSyncedEvent(this.uuId, this.businessUuId, this.strategyBoundary,
                        this.priceListVersion, this.isActive, this.lifecycleState, this.multiCurrencyPrices, auth)
        );
    }

    /**
     * SOC 2: Records a violation attempt without changing state.
     * Essential for security auditing of automated or unauthorized price shifts.
     */
    public void recordStrategyViolation(String attemptedStrategy, Actor actor) {
        // High-level auth check (Admin/System only)
        if (!actor.hasRole(Actor.ROLE_ADMIN) && !Actor.SYSTEM.equals(actor)) {
            throw new DomainAuthorizationException("Unauthorized violation recording.", "SEC-403", actor);
        }

        this.applyChange(actor,
                new PriceStrategyViolationAttemptedEvent(this.uuId, attemptedStrategy, actor),
                null
        );
    }

    // --- LIFECYCLE (Standardized) ---

    public void archive(Actor actor) { this.executeArchive(actor, new PriceListArchivedEvent(this.uuId, actor)); }
    public void unarchive(Actor actor) { this.executeUnarchive(actor, new PriceListUnarchivedEvent(this.uuId, actor)); }
    public void hardDelete(Actor actor) { this.executeHardDelete(actor, new PriceListHardDeletedEvent(this.uuId, actor)); }
    public void softDelete(Actor actor) { this.executeSoftDelete(actor, new PriceListSoftDeletedEvent(this.uuId, actor)); }
    public void restore(Actor actor) { this.executeRestore(actor, new PriceListRestoredEvent(this.uuId, actor)); }

    // --- GETTERS ---
    public PricingStrategyType getStrategyBoundary() { return strategyBoundary; }
    public PriceListVersion getPriceListVersion() { return priceListVersion; }
    public boolean isActive() { return isActive; }
    public Map<UuId, Map<Currency, PurchasePricing>> getMultiCurrencyPrices() {
        return Collections.unmodifiableMap(multiCurrencyPrices);

        // Identifiers and Metadata are handled by BaseAggregateRoot getters:
        // getUuId(), getBusinessUuId(), getOptLockVer(), etc.

    }

    public LifecycleState getLifecycleState() {
        return this.lifecycleState;
    }
}

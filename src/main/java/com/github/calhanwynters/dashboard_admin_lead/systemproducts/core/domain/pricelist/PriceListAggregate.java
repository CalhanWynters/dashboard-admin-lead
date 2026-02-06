package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel.PurchasePricing;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.*;

import java.util.*;

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
        var nextStatus = PriceListBehavior.evaluateActivation(this.isActive, true);
        this.applyChange(actor, new PriceListActivatedEvent(this.priceListUuId, actor), () -> this.isActive = nextStatus);
    }

    public void deactivate(Actor actor) {
        var nextStatus = PriceListBehavior.evaluateActivation(this.isActive, false);
        this.applyChange(actor, new PriceListDeactivatedEvent(this.priceListUuId, actor), () -> this.isActive = nextStatus);
    }

    public void addOrUpdatePrice(UuId targetId, Currency currency, PurchasePricing pricing, Actor actor) {
        PriceListBehavior.ensureActive(this.isActive);
        PriceListBehavior.validateStrategyMatch(this.strategyBoundary, pricing);
        var nextVersion = PriceListBehavior.evaluateVersionIncrement(this.priceListVersion);

        this.applyChange(actor,
                new PriceUpdatedEvent(this.priceListUuId, targetId, currency, pricing, nextVersion, actor),
                () -> {
                    this.priceListVersion = nextVersion;
                    this.multiCurrencyPrices.computeIfAbsent(targetId, k -> new HashMap<>()).put(currency, pricing);
                }
        );
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

    // Accessors
    public PriceListId getPriceListId() { return priceListId; }
    public boolean isActive() { return isActive; }
    public Map<UuId, Map<Currency, PurchasePricing>> getMultiCurrencyPrices() {
        return Collections.unmodifiableMap(multiCurrencyPrices);
    }
}

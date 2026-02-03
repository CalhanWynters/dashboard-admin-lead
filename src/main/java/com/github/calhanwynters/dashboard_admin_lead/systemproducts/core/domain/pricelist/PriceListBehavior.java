package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel.PurchasePricing;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * Orchestrates multi-currency price updates with mandatory actor attribution.
 */
public class PriceListBehavior {

    private final PriceListAggregate priceListAggregate;

    public PriceListBehavior(PriceListAggregate priceListAggregate) {
        DomainGuard.notNull(priceListAggregate, "PriceList instance");
        this.priceListAggregate = priceListAggregate;
    }

    /**
     * Injects or updates a specific currency price.
     */
    public PriceListAggregate addOrUpdatePrice(UuId targetId, Currency currency, PurchasePricing pricing, Actor actor) {
        priceListAggregate.updatePrice(targetId, currency, pricing, actor);
        return priceListAggregate;
    }

    /**
     * Removes a price and purges empty target entries.
     */
    public PriceListAggregate removePrice(UuId targetId, Currency currency, Actor actor) {
        priceListAggregate.removePrice(targetId, currency, actor);
        return priceListAggregate;
    }

    /**
     * Internal helper to synchronize versioning and auditing.
     */
    private void applyChanges(Map<UuId, Map<Currency, PurchasePricing>> newPrices, Actor actor) {
        priceListAggregate.updatePricesInternal(newPrices);
        priceListAggregate.incrementVersion();

        // Call the bridge method instead of recordUpdate directly
        priceListAggregate.triggerAuditUpdate(actor);
    }


}

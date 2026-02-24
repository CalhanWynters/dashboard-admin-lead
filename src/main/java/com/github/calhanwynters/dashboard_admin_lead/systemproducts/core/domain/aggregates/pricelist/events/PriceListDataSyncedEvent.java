package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.events;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PurchasePricing;
import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

import java.util.Currency;
import java.util.Map; // Or whatever collection type multiCurrencyPrices uses

@DomainEvent(name = "Price List Data Synced", namespace = "pricelist")
public record PriceListDataSyncedEvent(
        PriceListUuId priceListUuId,
        PriceListBusinessUuId priceListBusinessUuId,
        Class<? extends PurchasePricing> strategyBoundary, // Match the Class type
        PriceListVersion priceListVersion,
        boolean isActive,
        ProductBooleans productBooleans,
        Map<UuId, Map<Currency, PurchasePricing>> multiCurrencyPrices, // Match the nested Map
        Actor actor
) { }

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.events;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent(name = "PriceList Business ID Changed", namespace = "priceList")
public record PriceListBusinessUuIdChangedEvent(
        PriceListUuId priceListUuId,
        PriceListBusinessUuId oldId,
        PriceListBusinessUuId newId,
        Actor actor
) {}

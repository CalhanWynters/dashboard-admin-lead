package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

import java.util.Currency;

@DomainEvent(name = "Price Removed", namespace = "pricelist")
public record PriceRemovedEvent(
        PriceListUuId id,
        UuId targetId,
        Currency currency,
        PriceListVersion newVersion,
        Actor actor
) {}
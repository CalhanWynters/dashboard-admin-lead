package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

@DomainEvent(name = "Price List Strategy Changed", namespace = "pricelist")
public record PriceListStrategyChangedEvent(
        PriceListUuId id,
        String oldStrategyName,
        String newStrategyName,
        Actor actor
) {}
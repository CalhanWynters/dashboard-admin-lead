package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

@DomainEvent(name = "Price List Created", namespace = "pricelist")
public record PriceListCreatedEvent(
        PriceListUuId id,
        PriceListBusinessUuId businessId,
        Actor creator
) {}
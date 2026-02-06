package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.*;

@DomainEvent(name = "Price List Hard Deleted", namespace = "pricelist")
public record PriceListHardDeletedEvent(PriceListUuId id, Actor actor) {}
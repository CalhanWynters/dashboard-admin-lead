package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.*;

@DomainEvent(name = "Price List Soft Deleted", namespace = "pricelist")
public record PriceListSoftDeletedEvent(PriceListUuId id, Actor actor) {}

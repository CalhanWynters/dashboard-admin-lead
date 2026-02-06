package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.*;

import java.util.Currency;

/*
 * Usually, price lists aren't just deleted; they are toggled. If a price list represents a
 * "Seasonal Sale," you need events to trigger the opening and closing of that pricing window.
 */

@DomainEvent(name = "Price List Deactivated", namespace = "pricelist")
public record PriceListDeactivatedEvent(PriceListUuId id, Actor actor) {}
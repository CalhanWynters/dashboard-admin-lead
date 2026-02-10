package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Domain Event fired when a Price List is removed from the archive.
 */
@DomainEvent(name = "Price List Unarchived", namespace = "pricelist")
public record PriceListUnarchivedEvent(PriceListUuId priceListId, Actor actor) {}

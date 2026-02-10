package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Domain Event fired when a Price List is moved to the archive.
 * Required for SOC 2 lifecycle traceability.
 */
@DomainEvent(name = "Price List Archived", namespace = "pricelist")
public record PriceListArchivedEvent(PriceListUuId priceListId, Actor actor) {}

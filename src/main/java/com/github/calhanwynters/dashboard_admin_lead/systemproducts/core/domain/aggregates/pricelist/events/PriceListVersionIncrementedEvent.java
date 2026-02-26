package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

/**
 * Event fired when a Price List business version is explicitly incremented.
 * Tracking iterations is critical for billing audit trails.
 */
@DomainEvent(name = "Price List Version Incremented", namespace = "pricelist")
public record PriceListVersionIncrementedEvent(
        PriceListUuId priceListId,
        PriceListVersion newVersion,
        Actor actor
) {}
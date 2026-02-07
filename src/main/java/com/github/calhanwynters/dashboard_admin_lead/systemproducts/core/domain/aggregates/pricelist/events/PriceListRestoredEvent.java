package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

/**
 * Domain Event emitted when a Price List is restored from a soft-deleted state.
 * Required for SOC 2 lifecycle traceability.
 */
@DomainEvent(name = "Price List Restored", namespace = "pricelist")
public record PriceListRestoredEvent(PriceListUuId id, Actor actor) {}

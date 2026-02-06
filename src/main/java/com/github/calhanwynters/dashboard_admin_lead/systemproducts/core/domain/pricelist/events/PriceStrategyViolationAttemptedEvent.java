package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.*;

/*
 * Since you have a strategyBoundary, if a user tries to inject a TieredPricing object into an
 * aggregate restricted to FlatPricing, an event helps audit whether this was a user error or an
 * attempted system bypass.
 */

@DomainEvent(name = "Price Strategy Violation Attempted", namespace = "pricelist")
public record PriceStrategyViolationAttemptedEvent(PriceListUuId id, String attemptedStrategy, Actor actor) {}

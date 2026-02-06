package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.*;

/*
 * If a user wants to "Remove all types" from the list, firing one single "Cleared" event is
 * much more efficient than firing 50 TypeDetachedEvents. It also provides better intent for
 * the Audit Trail.
 */
@DomainEvent(name = "Type List Cleared", namespace = "typelist")
public record TypeListClearedEvent(TypeListUuId typeListId, Actor actor) {}

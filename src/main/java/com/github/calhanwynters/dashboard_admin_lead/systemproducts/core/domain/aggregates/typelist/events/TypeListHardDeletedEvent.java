package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;

@DomainEvent(name = "Type List Hard Deleted", namespace = "typelist")
public record TypeListHardDeletedEvent(TypeListUuId typeListUuId, Actor actor) {}
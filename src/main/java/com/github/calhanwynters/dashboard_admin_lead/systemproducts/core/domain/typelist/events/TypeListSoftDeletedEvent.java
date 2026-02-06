package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.*;

@DomainEvent(name = "Type List Soft Deleted", namespace = "typelist")
public record TypeListSoftDeletedEvent(TypeListUuId typeListUuId, Actor actor) {}

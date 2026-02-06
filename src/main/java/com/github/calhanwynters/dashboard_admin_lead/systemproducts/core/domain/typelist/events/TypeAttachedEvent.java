package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.TypesUuId;

@DomainEvent(name = "Type Attached", namespace = "typelist")
public record TypeAttachedEvent(TypeListUuId typeListId, TypesUuId typeId, Actor actor) {}

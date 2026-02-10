package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;

@DomainEvent(name = "Type List Unarchived", namespace = "typelist")
public record TypeListUnarchivedEvent(TypeListUuId typeListUuId, Actor actor) {}

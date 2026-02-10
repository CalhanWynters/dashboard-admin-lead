package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;

@DomainEvent(name = "Type List Archived", namespace = "typelist")
public record TypeListArchivedEvent(TypeListUuId typeListUuId, Actor actor) {}

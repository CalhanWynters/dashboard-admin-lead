package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;

@DomainEvent(name = "Type Unarchived", namespace = "types")
public record TypeUnarchivedEvent(TypesUuId typesUuId, Actor actor) {}

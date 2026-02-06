package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

@DomainEvent(name = "Type Soft Deleted", namespace = "types")
public record TypeSoftDeletedEvent(TypesUuId typesUuId, Actor actor) {}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.events;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent(name = "Type Region Updated", namespace = "types")
public record TypesRegionUpdatedEvent(
        TypesUuId id,
        TypesRegion oldRegion,
        TypesRegion newRegion,
        Actor actor
) { }

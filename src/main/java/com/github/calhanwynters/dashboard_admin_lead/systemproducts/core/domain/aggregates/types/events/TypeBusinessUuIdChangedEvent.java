package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.events;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent(name = "Types Business ID Changed", namespace = "Types")
public record TypeBusinessUuIdChangedEvent(
        TypesUuId typesUuId,
        TypesBusinessUuId oldId,
        TypesBusinessUuId newId,
        Actor actor
) {}

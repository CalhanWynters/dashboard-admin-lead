package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

@DomainEvent(name = "Type Specs Conflict Detected", namespace = "types")
public record TypeSpecsConflictDetectedEvent(
        TypesUuId typesUuId,
        String conflictDetails,
        Actor actor
) {}

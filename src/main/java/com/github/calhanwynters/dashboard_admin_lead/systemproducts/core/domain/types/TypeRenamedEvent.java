package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.*;

@DomainEvent(name = "Type Renamed", namespace = "types")
public record TypeRenamedEvent(
        TypesUuId typesUuId,
        TypesName newName,
        Actor actor
) {}

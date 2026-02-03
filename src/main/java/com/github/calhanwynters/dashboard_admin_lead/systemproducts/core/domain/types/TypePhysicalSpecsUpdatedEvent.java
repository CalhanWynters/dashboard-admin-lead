package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.*;


@DomainEvent(name = "Type Physical Specs Updated", namespace = "types")
public record TypePhysicalSpecsUpdatedEvent(
        TypesUuId typesUuId,
        TypesPhysicalSpecs newSpecs,
        Actor actor
) {}
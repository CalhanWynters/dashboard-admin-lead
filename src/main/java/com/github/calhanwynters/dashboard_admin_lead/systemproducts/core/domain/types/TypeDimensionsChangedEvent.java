package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.*;

@DomainEvent(name = "Type Dimensions Changed", namespace = "types")
public record TypeDimensionsChangedEvent(TypesUuId typesUuId, Actor actor) {}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.*;

@DomainEvent(name = "Type Hard Deleted", namespace = "types")
public record TypeHardDeletedEvent(TypesUuId typesUuId, Actor actor) {}
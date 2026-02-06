package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.events;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import org.jmolecules.event.annotation.DomainEvent;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.*;

@DomainEvent(name = "Type List Created", namespace = "typelist")
public record TypeListCreatedEvent(
        TypeListUuId typeListId,
        TypeListBusinessUuId businessId,
        Actor creator
) {}

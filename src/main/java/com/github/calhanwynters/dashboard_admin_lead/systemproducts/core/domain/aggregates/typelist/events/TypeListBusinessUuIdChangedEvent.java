package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.events;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent(name = "TypeList Business ID Changed", namespace = "typeList")
public record TypeListBusinessUuIdChangedEvent(
        TypeListUuId typeListUuId,
        TypeListBusinessUuId oldId,
        TypeListBusinessUuId newId,
        Actor actor
) {}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.TypesUuId;

@DomainEvent(name = "Type Detached", namespace = "typelist")
public record TypeDetachedEvent(
        TypeListUuId typeListUuId,
        TypesUuId detachedTypeUuId,
        Actor actor
) {}
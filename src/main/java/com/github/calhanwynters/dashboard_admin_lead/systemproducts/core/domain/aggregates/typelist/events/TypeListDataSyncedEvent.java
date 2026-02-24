package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.events;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper;
import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;
import java.util.Set;

@DomainEvent(name = "Type List Data Synced", namespace = "typelist")
public record TypeListDataSyncedEvent(
        TypeListUuId typeListUuId,
        TypeListBusinessUuId typeListBusinessUuId,
        Set<TypesDomainWrapper.TypesUuId> typeUuIds,
        ProductBooleans productBooleans,
        Actor actor
) { }

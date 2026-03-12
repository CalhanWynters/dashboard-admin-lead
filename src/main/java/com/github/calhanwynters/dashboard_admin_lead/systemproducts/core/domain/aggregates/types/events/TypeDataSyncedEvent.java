package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState; // Migrated from LEGACY
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

@DomainEvent(name = "Type Data Synced", namespace = "types")
public record TypeDataSyncedEvent(
        TypesUuId typesUuId,
        TypesBusinessUuId typesBusinessUuId,
        TypesName typesName,
        TypesPhysicalSpecs typesPhysicalSpecs,
        LifecycleState lifecycleState, // Standardized 2026 Type
        Actor actor
) { }

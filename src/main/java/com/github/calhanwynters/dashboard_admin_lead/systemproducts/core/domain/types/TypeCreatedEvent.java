package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.*;

/**
 * Signals the creation of a new Product Type.
 */
@DomainEvent(name = "Type Created", namespace = "types")
public record TypeCreatedEvent(
        TypesUuId typeId,
        TypesBusinessUuId businessId,
        Actor creator
) {}

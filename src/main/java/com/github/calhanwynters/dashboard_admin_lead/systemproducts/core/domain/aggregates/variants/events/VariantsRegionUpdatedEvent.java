package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.events;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent(name = "Variant Region Updated", namespace = "variants")
public record VariantsRegionUpdatedEvent(
        VariantsUuId id,
        VariantsRegion oldRegion,
        VariantsRegion newRegion,
        Actor actor
) { }
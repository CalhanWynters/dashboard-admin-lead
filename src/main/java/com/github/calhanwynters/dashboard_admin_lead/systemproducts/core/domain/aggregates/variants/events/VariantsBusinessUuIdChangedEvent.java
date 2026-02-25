package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.events;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent(name = "Variants Business ID Changed", namespace = "variants")
public record VariantsBusinessUuIdChangedEvent(
        VariantsUuId variantsUuId,
        VariantsBusinessUuId oldId,
        VariantsBusinessUuId newId,
        Actor actor
) {}

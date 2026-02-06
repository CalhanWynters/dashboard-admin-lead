package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;

@DomainEvent(name = "Variant Business UUID Changed", namespace = "variants")
public record VariantBusinessUuIdChangedEvent(
        VariantsUuId variantId,
        VariantsBusinessUuId oldId,
        VariantsBusinessUuId newId,
        Actor actor
) {}
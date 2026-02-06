package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;

@DomainEvent(name = "Variant Renamed", namespace = "variants")
public record VariantRenamedEvent(VariantsUuId variantId, VariantsName newName, Actor actor) {}

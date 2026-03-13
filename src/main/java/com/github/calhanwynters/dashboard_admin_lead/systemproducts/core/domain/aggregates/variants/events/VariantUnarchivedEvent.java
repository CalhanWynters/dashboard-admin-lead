package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.events;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper;
import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;

@DomainEvent(name = "Variant Unarchived", namespace = "variants")
public record VariantUnarchivedEvent(VariantsDomainWrapper.VariantsUuId variantsUuId, Actor actor) {}
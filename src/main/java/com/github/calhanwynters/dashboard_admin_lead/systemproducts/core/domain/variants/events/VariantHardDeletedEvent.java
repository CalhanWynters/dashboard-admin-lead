package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.*;

@DomainEvent(name = "Variant Hard Deleted", namespace = "variants")
public record VariantHardDeletedEvent(VariantsUuId variantId, Actor actor) {}

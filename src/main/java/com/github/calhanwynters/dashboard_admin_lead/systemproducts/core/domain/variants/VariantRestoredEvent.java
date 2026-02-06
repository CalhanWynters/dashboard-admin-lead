package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.*;

@DomainEvent(name = "Variant Restored", namespace = "variants")
public record VariantRestoredEvent(VariantsUuId variantId, Actor actor) {}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.*;

@DomainEvent(name = "Variant Usage Audit Requested", namespace = "variants")
public record VariantUsageAuditRequestedEvent(VariantsUuId variantId, Actor actor) {}

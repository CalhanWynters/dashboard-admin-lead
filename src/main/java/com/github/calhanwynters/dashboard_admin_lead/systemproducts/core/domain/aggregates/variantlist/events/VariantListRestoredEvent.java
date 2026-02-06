package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;

@DomainEvent(name = "Variant List Restored", namespace = "variantlist")
public record VariantListRestoredEvent(VariantListUuId variantListUuId, Actor actor) {}

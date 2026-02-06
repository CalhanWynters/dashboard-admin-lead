package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.*;

@DomainEvent(name = "Variant List Reordered", namespace = "variantlist")
public record VariantListReorderedEvent(VariantListUuId variantListUuId, Actor actor) {}
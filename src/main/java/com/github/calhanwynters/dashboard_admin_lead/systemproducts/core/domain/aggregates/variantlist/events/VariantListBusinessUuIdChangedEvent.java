package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.events;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent(name = "VariantList Business ID Changed", namespace = "variantList")
public record VariantListBusinessUuIdChangedEvent(
        VariantListUuId variantListUuId,
        VariantListBusinessUuId oldId,
        VariantListBusinessUuId newId,
        Actor actor
) {}

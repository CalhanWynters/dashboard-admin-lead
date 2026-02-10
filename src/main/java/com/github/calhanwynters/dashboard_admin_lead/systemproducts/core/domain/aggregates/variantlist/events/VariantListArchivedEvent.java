package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;

@DomainEvent(name = "Variant List Archived", namespace = "variantlist")
public record VariantListArchivedEvent(VariantListUuId variantListUuId, Actor actor) {}

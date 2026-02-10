package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.events;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper;
import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.ProductUuId;

@DomainEvent(name = "Variant Archived", namespace = "variants")
public record VariantArchivedEvent(VariantsDomainWrapper.VariantsUuId variantsUuId, Actor actor) {}

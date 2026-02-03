package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.*;

@DomainEvent(name = "Variant Renamed", namespace = "variants")
public record VariantRenamedEvent(VariantsUuId variantId, VariantsName newName, Actor actor) {}

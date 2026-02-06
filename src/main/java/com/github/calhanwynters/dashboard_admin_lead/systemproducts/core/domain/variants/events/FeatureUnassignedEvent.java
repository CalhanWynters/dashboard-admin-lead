package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.*;

@DomainEvent(name = "Feature Unassigned from Variant", namespace = "variants")
public record FeatureUnassignedEvent(VariantsUuId variantId, FeatureUuId featureId, Actor actor) {}
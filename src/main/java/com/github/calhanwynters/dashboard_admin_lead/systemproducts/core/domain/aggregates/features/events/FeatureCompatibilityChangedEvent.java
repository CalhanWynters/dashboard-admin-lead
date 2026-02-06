package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

@DomainEvent(name = "Feature Compatibility Changed", namespace = "features")
public record FeatureCompatibilityChangedEvent(
        FeatureUuId featureUuId,
        FeatureLabel oldTag,
        FeatureLabel newTag,
        Actor actor
) {}
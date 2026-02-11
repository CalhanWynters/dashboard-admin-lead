package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

@DomainEvent(name = "Feature Compatibility Tag Updated", namespace = "features")
public record FeatureCompTagUpdatedEvent(
        FeatureUuId featureUuId,
        FeatureLabel newTag,
        Actor actor
) {}

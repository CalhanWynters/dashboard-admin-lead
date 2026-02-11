package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

@DomainEvent(name = "Feature Name Updated", namespace = "features")
public record FeatureNameUpdatedEvent(
        FeatureUuId featureUuId,
        FeatureName newName,
        Actor actor
) {}

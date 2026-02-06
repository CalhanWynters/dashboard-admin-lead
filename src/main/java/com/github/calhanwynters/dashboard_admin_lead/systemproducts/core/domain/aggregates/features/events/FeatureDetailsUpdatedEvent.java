package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

@DomainEvent(name = "Feature Details Updated", namespace = "features")
public record FeatureDetailsUpdatedEvent(
        FeatureUuId featureUuId,
        FeatureName newName,
        FeatureLabel newTag,
        Actor actor
) {}
package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.*;

@DomainEvent(name = "Feature Created", namespace = "features")
public record FeatureCreatedEvent(
        FeatureUuId featureUuId,
        FeatureBusinessUuId businessUuId,
        Actor actor
) {}
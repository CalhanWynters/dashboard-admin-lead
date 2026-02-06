package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.*;

@DomainEvent(name = "Feature Hard Deleted", namespace = "features")
public record FeatureHardDeletedEvent(FeatureUuId featureUuId, Actor actor) {}
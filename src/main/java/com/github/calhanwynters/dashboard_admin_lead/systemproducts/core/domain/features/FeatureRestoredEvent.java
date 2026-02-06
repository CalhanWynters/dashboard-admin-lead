package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.*;


@DomainEvent(name = "Feature Restored", namespace = "features")
public record FeatureRestoredEvent(FeatureUuId featureUuId, Actor actor) {}
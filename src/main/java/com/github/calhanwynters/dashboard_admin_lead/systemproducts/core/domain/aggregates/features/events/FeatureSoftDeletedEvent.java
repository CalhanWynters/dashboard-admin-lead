package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;


@DomainEvent(name = "Feature Soft Deleted", namespace = "features")
public record FeatureSoftDeletedEvent(FeatureUuId featureUuId, Actor actor) {}

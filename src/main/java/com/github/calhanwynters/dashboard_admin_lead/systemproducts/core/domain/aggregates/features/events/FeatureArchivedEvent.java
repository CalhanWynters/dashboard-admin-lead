package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;

/**
 * Domain Event fired when a feature is moved to the archive.
 */
@DomainEvent(name = "Feature Archived", namespace = "features")
public record FeatureArchivedEvent(FeatureUuId featuresUuId, Actor actor) {}

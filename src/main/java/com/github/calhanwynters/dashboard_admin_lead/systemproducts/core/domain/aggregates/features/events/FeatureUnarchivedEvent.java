package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;

/**
 * Domain Event fired when a feature is removed from the archive.
 * Required for SOC 2 lifecycle traceability.
 */
@DomainEvent(name = "Feature Unarchived", namespace = "features")
public record FeatureUnarchivedEvent(FeatureUuId featuresUuId, Actor actor) {}

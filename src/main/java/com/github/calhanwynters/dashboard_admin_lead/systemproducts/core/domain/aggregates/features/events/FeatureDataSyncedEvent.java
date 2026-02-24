package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

/**
 * Domain Event fired to synchronize the full state of a feature to external systems (Kafka).
 */
@DomainEvent(name = "Feature Data Synced", namespace = "features")
public record FeatureDataSyncedEvent(
        FeatureUuId featuresUuId,
        FeatureBusinessUuId featuresBusinessUuId,
        FeatureName featuresName,
        FeatureLabel compatibilityTag,
        ProductBooleans productBooleans,
        Actor actor
) {}

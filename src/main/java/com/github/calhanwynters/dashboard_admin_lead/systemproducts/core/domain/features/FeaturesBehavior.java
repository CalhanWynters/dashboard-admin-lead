package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureName;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureLabel;

/**
 * Orchestrates updates for Feature Aggregates.
 * Ensures all modifications refresh the audit trail via the Actor.
 */
public class FeaturesBehavior {

    private final FeaturesAggregate feature;

    public FeaturesBehavior(FeaturesAggregate feature) {
        DomainGuard.notNull(feature, "Features Aggregate instance");
        this.feature = feature;
    }

    /**
     * Updates the name and compatibility tag of the feature.
     */
    public FeaturesAggregate updateDetails(FeatureName newName, FeatureLabel newTag, Actor actor) {
        DomainGuard.notNull(newName, "New Feature Name");
        DomainGuard.notNull(newTag, "New Compatibility Tag");
        DomainGuard.notNull(actor, "Actor performing the update");

        // 1. Update domain fields
        feature.updateDetails(newName, newTag);

        // 2. Refresh audit trail
        feature.recordUpdate(actor);

        return feature;
    }
}

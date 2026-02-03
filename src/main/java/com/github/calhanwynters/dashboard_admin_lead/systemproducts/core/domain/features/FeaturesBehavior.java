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
        // Behavior service still validates the actor exists before calling the aggregate
        DomainGuard.notNull(actor, "Actor performing the update");

        // Delegate the entire atomic operation to the aggregate
        feature.updateDetails(newName, newTag, actor);

        return feature;
    }
}

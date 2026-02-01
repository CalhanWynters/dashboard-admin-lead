package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureLabel;

import java.util.Set;

/**
 * Defines a relationship where selecting a specific feature or type
 * makes another feature invalid.
 */
public record IncompatibilityRule(
        UuId triggerUuId,            // Can be FeatureUuId or TypesUuId underlying value
        FeatureLabel triggerTag,     // Aligned with FeaturesAggregate.compatibilityTag
        FeatureUuId forbiddenFeatureUuId
) {
    public IncompatibilityRule {
        DomainGuard.notNull(forbiddenFeatureUuId, "Forbidden Feature ID");

        if (triggerUuId == null && triggerTag == null) {
            throw new IllegalArgumentException("Rule must have either a trigger ID or a trigger Tag.");
        }
    }

    /**
     * Determines if this rule is active based on the user's current selections.
     *
     * @param selectedIds   The set of currently active UuIds (extracted from TypesUuId and FeatureUuId).
     * @param selectedTags  The set of active compatibility FeatureLabels.
     */
    public boolean isTriggeredBy(Set<UuId> selectedIds, Set<FeatureLabel> selectedTags) {
        boolean idTriggered = triggerUuId != null && selectedIds.contains(triggerUuId);
        boolean tagTriggered = triggerTag != null && selectedTags.contains(triggerTag);

        return idTriggered || tagTriggered;
    }
}

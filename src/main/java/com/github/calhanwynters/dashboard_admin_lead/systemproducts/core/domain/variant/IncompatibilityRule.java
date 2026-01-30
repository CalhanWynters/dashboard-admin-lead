package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.Label;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.validationchecks.DomainGuard;

import java.util.Set;

/**
 * Defines a relationship where selecting a specific feature or type
 * makes another feature invalid.
 */
public record IncompatibilityRule(
        UuId triggerUuId,
        Label triggerTag,
        UuId forbiddenFeatureUuId
) {
    public IncompatibilityRule {
        // Enforce 2026 Integrity: A rule must have at least one trigger
        DomainGuard.notNull(forbiddenFeatureUuId, "Forbidden Feature ID");
        if (triggerUuId == null && triggerTag == null) {
            throw new IllegalArgumentException("Rule must have either a trigger ID or a trigger Tag.");
        }
    }

    /**
     * Determines if this rule is active based on the user's current selections.
     *
     * @param selectedIds   The set of currently active UuIds (Types and Features).
     * @param selectedTags  The set of active compatibility Labels (Tags).
     */
    public boolean isTriggeredBy(Set<UuId> selectedIds, Set<Label> selectedTags) {
        boolean idTriggered = triggerUuId != null && selectedIds.contains(triggerUuId);
        boolean tagTriggered = triggerTag != null && selectedTags.contains(triggerTag);

        return idTriggered || tagTriggered;
    }
}

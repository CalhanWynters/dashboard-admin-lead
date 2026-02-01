package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.variant;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.Description;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.Label;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.validationchecks.DomainGuard;

public record Feature(
        UuId featureUuId,
        Name featureName,
        Label compatibilityTag,
        Description featureDescription
) {
    public Feature {
        // 1. Existence & Nullability
        DomainGuard.notNull(featureUuId, "Feature ID");
        DomainGuard.notNull(featureName, "Feature featuresName");
        DomainGuard.notNull(compatibilityTag, "Compatibility Tag");
        DomainGuard.notNull(featureDescription, "Feature Description");

        // 2. Semantic Integrity
        // Ensure description is not a lazy copy of the featuresName
        DomainGuard.ensure(
                !featureName.value().equalsIgnoreCase(featureDescription.text()),
                "Feature description must provide additional context beyond the featuresName.",
                "VAL-022", "SEMANTICS"
        );
    }
}

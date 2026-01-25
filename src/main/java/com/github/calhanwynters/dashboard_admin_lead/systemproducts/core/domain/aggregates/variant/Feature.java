package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.Description;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.Label;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.Name;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.money.PurchasePricing;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.validationchecks.DomainGuard;

public record Feature(
        UuId featureUuId,
        Name featureName,
        Label compatibilityTag,
        Description featureDescription,
        PurchasePricing pricingModel
) {
    public Feature {
        // 1. Existence & Nullability
        DomainGuard.notNull(featureUuId, "Feature ID");
        DomainGuard.notNull(featureName, "Feature Name");
        DomainGuard.notNull(compatibilityTag, "Compatibility Tag");
        DomainGuard.notNull(featureDescription, "Feature Description");
        DomainGuard.notNull(pricingModel, "Pricing Model");

        // 2. Semantic Integrity
        // Ensure description is not a lazy copy of the name
        DomainGuard.ensure(
                !featureName.value().equalsIgnoreCase(featureDescription.text()),
                "Feature description must provide additional context beyond the name.",
                "VAL-022", "SEMANTICS"
        );
    }
}

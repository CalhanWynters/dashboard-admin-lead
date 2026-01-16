package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.money.PurchasePricing;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.validationchecks.DomainGuard;

/**
 * A pure DDD Value Object representing a product feature.
 * Aligned with DomainGuard validation for 2026 Edition.
 */
public record Feature(
        Name featureName,
        Label featureLabel,
        Description featureDescription,
        PurchasePricing pricingModel
) {
    public Feature {
        // 1. Existence & Nullability
        DomainGuard.notNull(featureName, "Feature Name");
        DomainGuard.notNull(featureLabel, "Feature Label");
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

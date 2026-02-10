package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

/**
 * Modern Factory for Features Aggregates.
 * Centralizes the creation and reconstitution logic with mandatory auditing.
 */
public class FeaturesFactory {

    /**
     * Creation Factory
     * Used to bring a new Feature into existence with a fresh audit trail.
     */
    public static FeaturesAggregate create(FeatureBusinessUuId businessId, FeatureName name, FeatureLabel tag, Actor creator) {
        // Initial state is always false/false (not archived, not deleted)
        return new FeaturesAggregate(
                null, FeatureUuId.generate(), businessId, name, tag,
                new ProductBooleans(false, false), AuditMetadata.create(creator)
        );
    }

    public static FeaturesAggregate reconstitute(FeatureId id, FeatureUuId uuId, FeatureBusinessUuId businessId,
                                                 FeatureName name, FeatureLabel tag, ProductBooleans booleans, AuditMetadata auditMetadata) {
        return new FeaturesAggregate(id, uuId, businessId, name, tag, booleans, auditMetadata);
    }
}

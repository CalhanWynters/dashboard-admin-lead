package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleansLEGACY;
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
    public static FeaturesAggregateLEGACY create(FeatureBusinessUuId businessId, FeatureName name, FeatureLabel tag, Actor creator) {
        // Initial state is always false/false (not archived, not deleted)
        return new FeaturesAggregateLEGACY(
                null, FeatureUuId.generate(), businessId, name, tag,
                new ProductBooleansLEGACY(false, false), AuditMetadata.create(creator)
        );
    }

    public static FeaturesAggregateLEGACY reconstitute(FeatureId id, FeatureUuId uuId, FeatureBusinessUuId businessId,
                                                       FeatureName name, FeatureLabel tag, ProductBooleansLEGACY booleans, AuditMetadata auditMetadata) {
        return new FeaturesAggregateLEGACY(id, uuId, businessId, name, tag, booleans, auditMetadata);
    }
}

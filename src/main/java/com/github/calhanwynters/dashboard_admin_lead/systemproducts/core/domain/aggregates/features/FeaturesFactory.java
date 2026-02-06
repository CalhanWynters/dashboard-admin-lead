package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
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
    public static FeaturesAggregate create(
            FeatureBusinessUuId businessId,
            FeatureName name,
            FeatureLabel tag,
            Actor creator) {

        return new FeaturesAggregate(
                FeatureId.of(0L),            // Temporary PK for new entities
                FeatureUuId.generate(),      // Domain Identity
                businessId,
                name,
                tag,
                AuditMetadata.create(creator) // Initial Audit Trail
        );
    }

    /**
     * Reconstitution Factory
     * Used by Infrastructure Repositories to hydrate data from the database.
     */
    public static FeaturesAggregate reconstitute(
            FeatureId id,
            FeatureUuId uuId,
            FeatureBusinessUuId businessId,
            FeatureName name,
            FeatureLabel tag,
            AuditMetadata auditMetadata) {

        return new FeaturesAggregate(
                id,
                uuId,
                businessId,
                name,
                tag,
                auditMetadata // Preserves existing audit history
        );
    }
}

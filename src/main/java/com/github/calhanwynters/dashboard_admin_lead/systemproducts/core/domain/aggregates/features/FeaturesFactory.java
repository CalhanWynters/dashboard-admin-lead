package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

import java.time.OffsetDateTime;

/**
 * Refactored Features Factory (2026 Edition).
 * Centralizes the creation and reconstitution logic with 2026-standard types.
 */
public class FeaturesFactory {

    private FeaturesFactory() {}

    /**
     * Creation Factory
     * Used to bring a new Feature into existence with a fresh audit trail and validation.
     */
    public static FeaturesAggregate create(FeatureBusinessUuId businessId, FeatureName name, FeatureLabel tag, Actor creator) {
        FeatureUuId newUuId = FeatureUuId.generate();

        // SOC 2: Authority check before instantiation
        FeaturesBehavior.validateCreation(newUuId, businessId, name, tag, creator);

        return new FeaturesAggregate(
                null,
                newUuId,
                businessId,
                name,
                tag,
                AuditMetadata.create(creator),
                new LifecycleState(false, false), // Migrated from LEGACY booleans
                0L,
                1,
                null
        );
    }

    /**
     * Reconstitution Factory
     * Used by Repositories to restore state from Persistence.
     */
    public static FeaturesAggregate reconstitute(
            FeatureId id,
            FeatureUuId uuId,
            FeatureBusinessUuId businessId,
            FeatureName name,
            FeatureLabel tag,
            AuditMetadata auditMetadata,
            LifecycleState lifecycleState,
            Long optLockVer,
            Integer schemaVer,
            OffsetDateTime lastSyncedAt) {

        return new FeaturesAggregate(
                id, uuId, businessId, name, tag,
                auditMetadata, lifecycleState,
                optLockVer, schemaVer, lastSyncedAt
        );
    }
}

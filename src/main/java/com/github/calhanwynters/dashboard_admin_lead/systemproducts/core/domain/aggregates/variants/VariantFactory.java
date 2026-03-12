package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;

/**
 * Refactored Variant Factory (2026 Edition).
 * Orchestrates the creation and reconstitution of Variants with modern technical metadata.
 */
public class VariantFactory {

    private VariantFactory() {}

    /**
     * Creation Factory
     * Delegates to the Aggregate static factory to ensure the VariantCreatedEvent
     * is registered and SOC 2 creation authority is verified.
     */
    public static VariantsAggregate create(VariantsBusinessUuId bizId, VariantsName name, Actor creator) {
        // Delegate to ensure the VariantCreatedEvent is registered
        return VariantsAggregate.create(
                VariantsUuId.generate(),
                bizId,
                name,
                creator
        );
    }

    /**
     * Reconstitution Factory
     * Rebuilds the aggregate from persistence state with modern lifecycle and locking.
     */
    public static VariantsAggregate reconstitute(
            VariantsId id,
            VariantsUuId uuId,
            VariantsBusinessUuId bizId,
            VariantsName name,
            Set<FeatureUuId> features,
            AuditMetadata audit,
            LifecycleState lifecycleState,
            Long optLockVer,
            Integer schemaVer,
            OffsetDateTime lastSyncedAt) {

        return new VariantsAggregate(
                id,
                uuId,
                bizId,
                name,
                (features != null) ? features : new HashSet<>(),
                audit,
                lifecycleState,
                optLockVer,
                schemaVer,
                lastSyncedAt
        );
    }
}

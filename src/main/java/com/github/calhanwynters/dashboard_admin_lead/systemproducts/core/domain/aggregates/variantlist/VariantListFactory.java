package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Refactored VariantList Factory (2026 Edition).
 * Orchestrates creation and reconstitution of VariantLists with modern technical metadata.
 */
public class VariantListFactory {

    private VariantListFactory() {}

    /**
     * Creation Factory
     * Delegates to the Aggregate factory to ensure event registration
     * and SOC 2 creation authority are verified.
     */
    public static VariantListAggregate create(VariantListBusinessUuId bizId, Actor creator) {
        // Delegate to ensure the VariantListCreatedEvent is registered
        return VariantListAggregate.create(VariantListUuId.generate(), bizId, creator);
    }

    /**
     * Reconstitution Factory
     * Rebuilds the aggregate from persistence state with modern lifecycle and locking.
     */
    public static VariantListAggregate reconstitute(
            VariantListId id,
            VariantListUuId uuId,
            VariantListBusinessUuId bizId,
            Set<VariantsUuId> ids,
            AuditMetadata audit,
            LifecycleState lifecycleState,
            Long optLockVer,
            Integer schemaVer,
            OffsetDateTime lastSyncedAt) {

        return new VariantListAggregate(
                id,
                uuId,
                bizId,
                (ids != null) ? ids : new HashSet<>(),
                audit,
                lifecycleState,
                optLockVer,
                schemaVer,
                lastSyncedAt
        );
    }
}

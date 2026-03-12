package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

import java.time.OffsetDateTime;

/**
 * Refactored Types Factory (2026 Edition).
 * Orchestrates creation and reconstitution of Types with physical spec integrity.
 */
public class TypesFactory {

    private TypesFactory() {}

    /**
     * Creation Factory
     * Delegates to the Aggregate static factory to ensure the TypeCreatedEvent
     * is registered and SOC 2 creation authority is verified.
     */
    public static TypesAggregate create(
            TypesBusinessUuId bizId,
            TypesName name,
            TypesPhysicalSpecs physicalSpecs,
            Actor creator) {

        // Delegate to the Aggregate's internal factory for event registration
        return TypesAggregate.create(
                TypesUuId.generate(),
                bizId,
                name,
                physicalSpecs,
                creator
        );
    }

    /**
     * Reconstitution Factory
     * Rebuilds the aggregate from persistence state with modern technical metadata.
     */
    public static TypesAggregate reconstitute(
            TypesId id,
            TypesUuId uuId,
            TypesBusinessUuId bizId,
            TypesName name,
            TypesPhysicalSpecs physicalSpecs,
            AuditMetadata audit,
            LifecycleState lifecycleState,
            Long optLockVer,
            Integer schemaVer,
            OffsetDateTime lastSyncedAt) {

        return new TypesAggregate(
                id,
                uuId,
                bizId,
                name,
                physicalSpecs,
                audit,
                lifecycleState,
                optLockVer,
                schemaVer,
                lastSyncedAt
        );
    }
}

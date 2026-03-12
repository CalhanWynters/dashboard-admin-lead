package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Refactored TypeList Factory (2026 Edition).
 * Orchestrates creation and reconstitution of TypeLists with modern lifecycle and auditing.
 */
public class TypeListFactory {

    private TypeListFactory() {}

    /**
     * Creation Factory
     * Used to bring a new TypeList into existence with mandatory SOC 2 authority checks.
     */
    public static TypeListAggregate create(TypeListBusinessUuId bizId, Actor creator) {
        TypeListUuId newUuId = TypeListUuId.generate();

        // SOC 2: Authority and existence check via Behavior
        TypeListBehavior.validateCreation(newUuId, bizId, creator);

        return new TypeListAggregate(
                null,
                newUuId,
                bizId,
                new HashSet<>(),  // initial empty set of type IDs
                AuditMetadata.create(creator),
                new LifecycleState(false, false),
                0L,               // optLockVer
                1,                // schemaVer
                null              // lastSyncedAt
        );
    }

    /**
     * Reconstitution Factory
     * Restores state from persistence for structural integrity checks.
     */
    public static TypeListAggregate reconstitute(
            TypeListId id,
            TypeListUuId uuId,
            TypeListBusinessUuId bizId,
            Set<TypesUuId> ids,
            AuditMetadata audit,
            LifecycleState lifecycleState,
            Long optLockVer,
            Integer schemaVer,
            OffsetDateTime lastSyncedAt) {

        return new TypeListAggregate(
                id, uuId, bizId, ids, audit, lifecycleState,
                optLockVer, schemaVer, lastSyncedAt
        );
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.events.*;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;

/**
 * Refactored Variants Aggregate (2026 Edition).
 * Migrated from LEGACYBaseAggregateRoot to the Generic Orchestration Engine.
 */
public class VariantsAggregate extends BaseAggregateRoot<
        VariantsAggregate,
        VariantsId,
        VariantsUuId,
        VariantsBusinessUuId
        > {

    private VariantsName variantsName;
    private final Set<FeatureUuId> assignedFeatureUuIds;

    public VariantsAggregate(VariantsId id, VariantsUuId uuId, VariantsBusinessUuId businessUuId,
                             VariantsName name, Set<FeatureUuId> featureUuIds,
                             AuditMetadata auditMetadata, LifecycleState lifecycleState,
                             Long optLockVer, Integer schemaVer, OffsetDateTime lastSyncedAt) {
        super(id, uuId, businessUuId, auditMetadata, optLockVer, schemaVer, lastSyncedAt);
        this.variantsName = name;
        this.assignedFeatureUuIds = new HashSet<>(featureUuIds != null ? featureUuIds : Collections.emptySet());
        this.lifecycleState = lifecycleState;
    }

    // --- FACTORY ---

    public static VariantsAggregate create(VariantsUuId uuId, VariantsBusinessUuId bUuId,
                                           VariantsName name, Actor actor) {
        // Standardized validation call (requires the 3-arg fix in VariantsBehavior)
        VariantsBehavior.validateCreation(uuId, bUuId, actor);

        VariantsAggregate aggregate = new VariantsAggregate(
                null, uuId, bUuId, name, new HashSet<>(),
                AuditMetadata.create(actor), new LifecycleState(false, false),
                0L, 1, null
        );

        aggregate.registerEvent(new VariantCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void rename(VariantsName newName, Actor actor) {
        this.applyDomainChange(actor, newName,
                (next, auth) -> VariantsBehavior.evaluateRename(this.variantsName, next, auth),
                val -> new VariantRenamedEvent(this.uuId, val, actor),
                val -> this.variantsName = val
        );
    }

    public void updateBusinessUuId(VariantsBusinessUuId newId, Actor actor) {
        this.executeBusinessUuIdUpdate(newId, actor,
                val -> new VariantsBusinessUuIdChangedEvent(this.uuId, this.businessUuId, val, actor)
        );
    }

    public void assignFeature(FeatureUuId featureUuId, Actor actor) {
        this.applyDomainChange(actor, featureUuId,
                (next, auth) -> {
                    VariantsBehavior.ensureCanAssign(this.assignedFeatureUuIds, next, auth);
                    return next;
                },
                val -> new FeatureAssignedEvent(this.uuId, val, actor),
                this.assignedFeatureUuIds::add
        );
    }

    public void unassignFeature(FeatureUuId featureUuId, Actor actor) {
        this.applyDomainChange(actor, featureUuId,
                (next, auth) -> {
                    VariantsBehavior.ensureCanUnassign(this.assignedFeatureUuIds, next, auth);
                    return next;
                },
                val -> new FeatureUnassignedEvent(this.uuId, val, actor),
                this.assignedFeatureUuIds::remove
        );
    }

    public void unassignAllFeatures(Actor actor) {
        ensureActive(); // Handled by BaseAggregateRoot
        VariantsBehavior.verifyManagementAuthority(actor);

        if (this.assignedFeatureUuIds.isEmpty()) return;

        this.applyChange(actor,
                new AllFeaturesUnassignedEvent(this.uuId, actor),
                this.assignedFeatureUuIds::clear
        );
    }

    public void syncToKafka(Actor actor) {
        this.executeSync(actor,
                auth -> new VariantDataSyncedEvent(this.uuId, this.businessUuId, this.variantsName,
                        this.assignedFeatureUuIds, this.lifecycleState, auth)
        );
    }

    public void requestUsageAudit(Actor actor) {
        VariantsBehavior.verifyManagementAuthority(actor);
        // Pure event, no mutation (null runnable)
        this.applyChange(actor, new VariantUsageAuditRequestedEvent(this.uuId, actor), null);
    }

    // --- LIFECYCLE (Standardized via Base Engine) ---

    public void archive(Actor actor) {
        this.executeArchive(actor, new VariantArchivedEvent(this.uuId, actor));
    }

    public void unarchive(Actor actor) {
        this.executeUnarchive(actor, new VariantUnarchivedEvent(this.uuId, actor));
    }

    public void softDelete(Actor actor) {
        this.executeSoftDelete(actor, new VariantSoftDeletedEvent(this.uuId, actor));
    }

    public void restore(Actor actor) {
        this.executeRestore(actor, new VariantRestoredEvent(this.uuId, actor));
    }

    public void hardDelete(Actor actor) {
        this.executeHardDelete(actor, new VariantHardDeletedEvent(this.uuId, actor));
    }

    // --- GETTERS ---
    public VariantsName getVariantsName() { return variantsName; }
    public Set<FeatureUuId> getAssignedFeatureUuIds() { return Collections.unmodifiableSet(assignedFeatureUuIds); }
    public LifecycleState getLifecycleState() { return lifecycleState; }
}

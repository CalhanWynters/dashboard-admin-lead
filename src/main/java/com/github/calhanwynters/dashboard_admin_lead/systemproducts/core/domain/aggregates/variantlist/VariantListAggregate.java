package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.events.*;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;


/**
 * Aggregate Root for managing collections of Variants.
 * Follows Two-Liner Pattern with mandatory SOC 2 Authorizations.
 */

public class VariantListAggregate extends BaseAggregateRoot<
        VariantListAggregate,
        VariantListId,
        VariantListUuId,
        VariantListBusinessUuId
        > {

    private final Set<VariantsUuId> variantUuIds;

    public VariantListAggregate(VariantListId id, VariantListUuId uuId, VariantListBusinessUuId businessUuId,
                                Set<VariantsUuId> variantUuIds, AuditMetadata auditMetadata,
                                LifecycleState lifecycleState, Long optLockVer,
                                Integer schemaVer, OffsetDateTime lastSyncedAt) {
        super(id, uuId, businessUuId, auditMetadata, optLockVer, schemaVer, lastSyncedAt);
        this.variantUuIds = new HashSet<>(variantUuIds != null ? variantUuIds : Collections.emptySet());
        this.lifecycleState = lifecycleState;
    }

    // --- FACTORY ---

    public static VariantListAggregate create(VariantListUuId uuId, VariantListBusinessUuId bUuId, Actor actor) {
        VariantListBehavior.validateCreation(uuId, bUuId, actor);

        VariantListAggregate aggregate = new VariantListAggregate(
                null, uuId, bUuId, new HashSet<>(),
                AuditMetadata.create(actor), new LifecycleState(false, false),
                0L, 1, null
        );

        aggregate.registerEvent(new VariantListCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void updateBusinessUuId(VariantListBusinessUuId newId, Actor actor) {
        this.executeBusinessUuIdUpdate(newId, actor,
                val -> new VariantListBusinessUuIdChangedEvent(this.uuId, this.businessUuId, val, actor)
        );
    }

    public void attachVariant(VariantsUuId variantUuId, Actor actor) {
        this.applyDomainChange(actor, variantUuId,
                (next, auth) -> {
                    VariantListBehavior.ensureCanAttach(this.variantUuIds, next, auth);
                    return next;
                },
                val -> new VariantAttachedEvent(this.uuId, val, actor),
                this.variantUuIds::add
        );
    }

    public void detachVariant(VariantsUuId variantUuId, Actor actor) {
        this.applyDomainChange(actor, variantUuId,
                (next, auth) -> {
                    VariantListBehavior.ensureCanDetach(this.variantUuIds, next, auth);
                    return next;
                },
                val -> new VariantDetachedEvent(this.uuId, val, actor),
                this.variantUuIds::remove
        );
    }

    public void reorder(Actor actor) {
        ensureActive();
        VariantListBehavior.ensureCanReorder(this.variantUuIds, actor);

        // Pure domain event for ordering changes
        this.applyChange(actor, new VariantListReorderedEvent(this.uuId, actor), null);
    }

    public void clearAllVariants(Actor actor) {
        ensureActive();
        VariantListBehavior.verifyMembershipAuthority(actor);

        if (this.variantUuIds.isEmpty()) return;

        this.applyChange(actor,
                new VariantListClearedEvent(this.uuId, actor),
                this.variantUuIds::clear
        );
    }

    public void syncToKafka(Actor actor) {
        this.executeSync(actor,
                auth -> new VariantListDataSyncedEvent(this.uuId, this.businessUuId,
                        this.variantUuIds, this.lifecycleState, auth)
        );
    }

    // --- LIFECYCLE (Standardized via Base Engine) ---

    public void archive(Actor actor) {
        this.executeArchive(actor, new VariantListArchivedEvent(this.uuId, actor));
    }

    public void unarchive(Actor actor) {
        this.executeUnarchive(actor, new VariantListUnarchivedEvent(this.uuId, actor));
    }

    public void softDelete(Actor actor) {
        this.executeSoftDelete(actor, new VariantListSoftDeletedEvent(this.uuId, actor));
    }

    public void restore(Actor actor) {
        this.executeRestore(actor, new VariantListRestoredEvent(this.uuId, actor));
    }

    public void hardDelete(Actor actor) {
        this.executeHardDelete(actor, new VariantListHardDeletedEvent(this.uuId, actor));
    }

    // --- GETTERS ---
    public Set<VariantsUuId> getVariantUuIds() { return Collections.unmodifiableSet(variantUuIds); }
    public LifecycleState getLifecycleState() { return lifecycleState; }
}


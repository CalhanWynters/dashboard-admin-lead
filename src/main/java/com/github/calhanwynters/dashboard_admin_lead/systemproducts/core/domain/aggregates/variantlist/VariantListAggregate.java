package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.events.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Aggregate Root for managing collections of Variants.
 * Follows Two-Liner Pattern with mandatory SOC 2 Authorizations.
 */
public class VariantListAggregate extends BaseAggregateRoot<VariantListAggregate> {

    private final VariantListId variantListId;
    private final VariantListUuId variantListUuId;
    private final Set<VariantsUuId> variantUuIds;
    private boolean deleted;

    public VariantListAggregate(VariantListId variantListId,
                                VariantListUuId variantListUuId,
                                VariantListBusinessUuId variantListBusinessUuId,
                                Set<VariantsUuId> variantUuIds,
                                boolean deleted,
                                AuditMetadata auditMetadata) {
        super(auditMetadata);
        this.variantListId = variantListId;
        this.variantListUuId = DomainGuard.notNull(variantListUuId, "VariantList UUID");
        DomainGuard.notNull(variantListBusinessUuId, "Business UUID");
        this.variantUuIds = new HashSet<>(variantUuIds != null ? variantUuIds : Collections.emptySet());
        this.deleted = deleted;
    }

    public static VariantListAggregate create(VariantListUuId uuId, VariantListBusinessUuId bUuId, Actor actor) {
        // Line 1: Auth
        VariantListBehavior.verifyCreationAuthority(actor);

        VariantListAggregate aggregate = new VariantListAggregate(null, uuId, bUuId, new HashSet<>(), false, AuditMetadata.create(actor));
        aggregate.registerEvent(new VariantListCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void attachVariant(VariantsUuId variantUuId, Actor actor) {
        // Line 1: Logic & Auth
        VariantListBehavior.ensureActive(this.deleted);
        VariantListBehavior.ensureCanAttach(this.variantUuIds, variantUuId, actor);

        // Line 2: Side-Effect
        this.applyChange(actor,
                new VariantAttachedEvent(this.variantListUuId, variantUuId, actor),
                () -> this.variantUuIds.add(variantUuId)
        );
    }

    public void detachVariant(VariantsUuId variantUuId, Actor actor) {
        // Line 1: Logic & Auth
        VariantListBehavior.ensureActive(this.deleted);
        VariantListBehavior.ensureCanDetach(this.variantUuIds, variantUuId, actor);

        this.applyChange(actor,
                new VariantDetachedEvent(this.variantListUuId, variantUuId, actor),
                () -> this.variantUuIds.remove(variantUuId)
        );
    }

    public void reorder(Actor actor) {
        // Line 1: Logic & Auth
        VariantListBehavior.ensureActive(this.deleted);
        VariantListBehavior.ensureCanReorder(this.variantUuIds, actor);

        this.applyChange(actor, new VariantListReorderedEvent(this.variantListUuId, actor), null);
    }

    public void clearAllVariants(Actor actor) {
        // Line 1: Logic & Auth
        VariantListBehavior.ensureActive(this.deleted);
        VariantListBehavior.verifyMembershipAuthority(actor);

        if (this.variantUuIds.isEmpty()) return;

        this.applyChange(actor,
                new VariantListClearedEvent(this.variantListUuId, actor),
                this.variantUuIds::clear
        );
    }

    public void softDelete(Actor actor) {
        // Line 1: Auth
        VariantListBehavior.ensureActive(this.deleted);
        VariantListBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor, new VariantListSoftDeletedEvent(this.variantListUuId, actor), () -> this.deleted = true);
    }

    public void restore(Actor actor) {
        // Line 1: Auth
        if (!this.deleted) return;
        VariantListBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor, new VariantListRestoredEvent(this.variantListUuId, actor), () -> this.deleted = false);
    }

    public void hardDelete(Actor actor) {
        // Line 1: Admin Auth
        VariantListBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor, new VariantListHardDeletedEvent(this.variantListUuId, actor), null);
    }

    // --- ACCESSORS ---
    public boolean isDeleted() { return deleted; }
    public VariantListId getVariantListId() { return variantListId; }
    public VariantListUuId getVariantListUuId() { return variantListUuId; }
    public Set<VariantsUuId> getVariantUuIds() { return Collections.unmodifiableSet(variantUuIds); }
}

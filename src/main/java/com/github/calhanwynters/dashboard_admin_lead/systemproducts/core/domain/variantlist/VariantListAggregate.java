package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.VariantsUuId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
        // Ensure Business UUID is validated even if not stored as a field
        DomainGuard.notNull(variantListBusinessUuId, "Business UUID");
        this.variantUuIds = new HashSet<>(variantUuIds != null ? variantUuIds : Collections.emptySet());
        this.deleted = deleted;
    }

    public static VariantListAggregate create(VariantListUuId uuId, VariantListBusinessUuId bUuId, Actor actor) {
        VariantListAggregate aggregate = new VariantListAggregate(null, uuId, bUuId, new HashSet<>(), false, AuditMetadata.create(actor));
        aggregate.registerEvent(new VariantListCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void attachVariant(VariantsUuId variantUuId, Actor actor) {
        VariantListBehavior.ensureActive(this.deleted);
        VariantListBehavior.ensureCanAttach(this.variantUuIds, variantUuId);

        this.applyChange(actor,
                new VariantAttachedEvent(this.variantListUuId, variantUuId, actor),
                () -> this.variantUuIds.add(variantUuId)
        );
    }

    public void detachVariant(VariantsUuId variantUuId, Actor actor) {
        VariantListBehavior.ensureActive(this.deleted);
        VariantListBehavior.ensureCanDetach(this.variantUuIds, variantUuId);

        this.applyChange(actor,
                new VariantDetachedEvent(this.variantListUuId, variantUuId, actor),
                () -> this.variantUuIds.remove(variantUuId)
        );
    }

    public void reorder(Actor actor) {
        VariantListBehavior.ensureActive(this.deleted);
        VariantListBehavior.ensureCanReorder(this.variantUuIds);

        this.applyChange(actor, new VariantListReorderedEvent(this.variantListUuId, actor), null);
    }

    public void clearAllVariants(Actor actor) {
        VariantListBehavior.ensureActive(this.deleted);

        if (this.variantUuIds.isEmpty()) return;

        this.applyChange(actor,
                new VariantListClearedEvent(this.variantListUuId, actor),
                this.variantUuIds::clear
        );
    }

    public void softDelete(Actor actor) {
        VariantListBehavior.ensureActive(this.deleted);
        this.applyChange(actor, new VariantListSoftDeletedEvent(this.variantListUuId, actor), () -> this.deleted = true);
    }

    public void restore(Actor actor) {
        if (!this.deleted) return;
        this.applyChange(actor, new VariantListRestoredEvent(this.variantListUuId, actor), () -> this.deleted = false);
    }

    public void hardDelete(Actor actor) {
        this.applyChange(actor, new VariantListHardDeletedEvent(this.variantListUuId, actor), null);
    }

    // --- ACCESSORS ---
    public boolean isDeleted() { return deleted; }
    public VariantListId getVariantListId() { return variantListId; }
    public VariantListUuId getVariantListUuId() { return variantListUuId; }
    public Set<VariantsUuId> getVariantUuIds() { return Collections.unmodifiableSet(variantUuIds); }
}

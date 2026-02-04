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

/**
 * Aggregate Root for VariantList.
 * Manages the collection of associated Variants with mandatory actor attribution for all updates.
 */
public class VariantListAggregate extends BaseAggregateRoot<VariantListAggregate> {

    private final VariantListId variantListId;
    private final VariantListUuId variantListUuId;
    private final VariantListBusinessUuId variantListBusinessUuId;
    private final Set<VariantsUuId> variantUuIds;

    public VariantListAggregate(VariantListId variantListId,
                                VariantListUuId variantListUuId,
                                VariantListBusinessUuId variantListBusinessUuId,
                                Set<VariantsUuId> variantUuIds,
                                AuditMetadata auditMetadata) {
        super(auditMetadata);

        DomainGuard.notNull(variantListId, "VariantList ID");
        DomainGuard.notNull(variantListUuId, "VariantList UUID");
        DomainGuard.notNull(variantListBusinessUuId, "Business UUID");
        DomainGuard.notNull(variantUuIds, "Variant UUID Set");

        this.variantListId = variantListId;
        this.variantListUuId = variantListUuId;
        this.variantListBusinessUuId = variantListBusinessUuId;
        this.variantUuIds = new HashSet<>(variantUuIds);
    }

    // --- DOMAIN ACTIONS ---

    public void attachVariant(VariantsUuId variantUuId, Actor actor) {
        DomainGuard.notNull(variantUuId, "Variant UUID to attach");
        DomainGuard.notNull(actor, "Actor performing the update");

        if (this.variantUuIds.add(variantUuId)) {
            this.recordUpdate(actor);
            this.registerEvent(new VariantAttachedEvent(this.variantListUuId, variantUuId, actor));
        }
    }

    public void detachVariant(VariantsUuId variantUuId, Actor actor) {
        DomainGuard.notNull(variantUuId, "Variant UUID to detach");
        DomainGuard.notNull(actor, "Actor performing the update");

        if (this.variantUuIds.remove(variantUuId)) {
            this.recordUpdate(actor);
            this.registerEvent(new VariantDetachedEvent(this.variantListUuId, variantUuId, actor));
        }
    }

    public void softDelete(Actor actor) {
        DomainGuard.notNull(actor, "Actor");
        this.recordUpdate(actor);
        this.registerEvent(new VariantListSoftDeletedEvent(this.variantListUuId, actor));
    }

    public void hardDelete(Actor actor) {
        DomainGuard.notNull(actor, "Actor");
        // Hard delete usually implies immediate removal, but event triggers cleanup in other contexts
        this.registerEvent(new VariantListHardDeletedEvent(this.variantListUuId, actor));
    }

    // --- ACCESSORS ---
    public VariantListId getVariantListId() { return variantListId; }
    public VariantListUuId getVariantListUuId() { return variantListUuId; }
    public VariantListBusinessUuId getVariantListBusinessUuId() { return variantListBusinessUuId; }

    public Set<VariantsUuId> getVariantUuIds() {
        return Collections.unmodifiableSet(variantUuIds);
    }
}

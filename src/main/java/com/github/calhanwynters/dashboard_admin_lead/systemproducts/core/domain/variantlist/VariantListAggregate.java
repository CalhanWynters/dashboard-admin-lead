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

    /**
     * Attaches a variant and refreshes the audit trail in one atomic action.
     */
    public void attachVariant(VariantsUuId variantUuId, Actor actor) {
        DomainGuard.notNull(variantUuId, "Variant UUID to attach");
        DomainGuard.notNull(actor, "Actor performing the update");

        // Set logic ensures idempotency; we only audit if the collection actually changed
        if (this.variantUuIds.add(variantUuId)) {
            this.recordUpdate(actor);
        }
    }

    /**
     * Detaches a variant and refreshes the audit trail.
     */
    public void detachVariant(VariantsUuId variantUuId, Actor actor) {
        DomainGuard.notNull(variantUuId, "Variant UUID to detach");
        DomainGuard.notNull(actor, "Actor performing the update");

        if (this.variantUuIds.remove(variantUuId)) {
            this.recordUpdate(actor);
        }
    }

    // --- ACCESSORS ---
    public VariantListId getVariantListId() { return variantListId; }
    public VariantListUuId getVariantListUuId() { return variantListUuId; }
    public VariantListBusinessUuId getVariantListBusinessUuId() { return variantListBusinessUuId; }

    /**
     * Returns an unmodifiable view of variants to enforce the Rich Domain Model pattern.
     */
    public Set<VariantsUuId> getVariantUuIds() {
        return Collections.unmodifiableSet(variantUuIds);
    }
}

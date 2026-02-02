package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.VariantsUuId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

    /**
     * Bridge method for Behavior access to protected audit logic.
     */
    void triggerAuditUpdate(Actor actor) {
        this.recordUpdate(actor);
    }

    void addVariantInternal(VariantsUuId variantsUuId) {
        this.variantUuIds.add(variantsUuId);
    }

    // Getters
    public VariantListId getVariantListId() { return variantListId; }
    public VariantListUuId getVariantListUuId() { return variantListUuId; }
    public VariantListBusinessUuId getVariantListBusinessUuId() { return variantListBusinessUuId; }
    public Set<VariantsUuId> getVariantUuIds() { return Collections.unmodifiableSet(variantUuIds); }
}

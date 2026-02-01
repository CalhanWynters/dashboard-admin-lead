package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.VariantListId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.VariantListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.VariantListBusinessUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.VariantsUuId;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class VariantListAggregate extends AbstractAggregateRoot<VariantListAggregate> {

    private final VariantListId variantListId;
    private final VariantListUuId variantListUuId;
    private final VariantListBusinessUuId variantListBusinessUuId;
    private final Set<VariantsUuId> variantUuIds;

    public VariantListAggregate(VariantListId variantListId,
                                VariantListUuId variantListUuId,
                                VariantListBusinessUuId variantListBusinessUuId,
                                Set<VariantsUuId> variantUuIds) {
        // Validation checks
        DomainGuard.notNull(variantListId, "VariantListAggregate ID");
        DomainGuard.notNull(variantListUuId, "VariantListAggregate UUID");
        DomainGuard.notNull(variantListBusinessUuId, "VariantListAggregate Business UUID");
        DomainGuard.notNull(variantUuIds, "Variant UUID Set");

        this.variantListId = variantListId;
        this.variantListUuId = variantListUuId;
        this.variantListBusinessUuId = variantListBusinessUuId;
        // Defensive copy
        this.variantUuIds = new HashSet<>(variantUuIds);
    }

    // Getters
    public VariantListId getVariantListId() {
        return variantListId;
    }

    public VariantListUuId getVariantListUuId() {
        return variantListUuId;
    }

    public VariantListBusinessUuId getVariantListBusinessUuId() {
        return variantListBusinessUuId;
    }

    public Set<VariantsUuId> getVariantUuIds() {
        return Collections.unmodifiableSet(variantUuIds);
    }

    /**
     * Updates the list and provides a hook for Spring Data
     * AbstractAggregateRoot events.
     */
    public void addVariant(VariantsUuId variantsUuId) {
        DomainGuard.notNull(variantsUuId, "Variant UUID");
        this.variantUuIds.add(variantsUuId);
    }
}

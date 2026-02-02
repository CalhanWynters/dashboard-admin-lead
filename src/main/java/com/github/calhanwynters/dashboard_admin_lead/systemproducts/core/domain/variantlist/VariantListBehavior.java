package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.VariantsUuId;

public class VariantListBehavior {

    private final VariantListAggregate variantList;

    public VariantListBehavior(VariantListAggregate variantList) {
        DomainGuard.notNull(variantList, "VariantList instance");
        this.variantList = variantList;
    }

    public VariantListAggregate attachVariant(VariantsUuId variantUuId, Actor actor) {
        DomainGuard.notNull(variantUuId, "Variant UUID to attach");

        variantList.addVariantInternal(variantUuId);
        variantList.triggerAuditUpdate(actor);

        return variantList;
    }
}

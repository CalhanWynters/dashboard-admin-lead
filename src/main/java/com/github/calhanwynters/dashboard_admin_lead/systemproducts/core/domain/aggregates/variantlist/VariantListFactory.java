package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;

import java.util.Set;

public class VariantListFactory {

    public static VariantListAggregate create(VariantListBusinessUuId bizId, Actor creator) {
        return VariantListAggregate.create(VariantListUuId.generate(), bizId, creator);
    }

    public static VariantListAggregate reconstitute(
            VariantListId id,
            VariantListUuId uuId,
            VariantListBusinessUuId bizId,
            Set<VariantsUuId> ids,
            ProductBooleans productBooleans, // Replaced boolean
            AuditMetadata audit) {

        return new VariantListAggregate(id, uuId, bizId, ids, productBooleans, audit);
    }
}

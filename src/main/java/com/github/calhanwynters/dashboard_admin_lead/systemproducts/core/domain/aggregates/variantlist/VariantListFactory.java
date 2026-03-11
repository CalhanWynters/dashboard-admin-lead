package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleansLEGACY;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;

import java.util.Set;

public class VariantListFactory {

    public static VariantListAggregateLEGACY create(VariantListBusinessUuId bizId, Actor creator) {
        return VariantListAggregateLEGACY.create(VariantListUuId.generate(), bizId, creator);
    }

    public static VariantListAggregateLEGACY reconstitute(
            VariantListId id,
            VariantListUuId uuId,
            VariantListBusinessUuId bizId,
            Set<VariantsUuId> ids,
            ProductBooleansLEGACY productBooleansLEGACY, // Replaced boolean
            AuditMetadata audit) {

        return new VariantListAggregateLEGACY(id, uuId, bizId, ids, productBooleansLEGACY, audit);
    }
}

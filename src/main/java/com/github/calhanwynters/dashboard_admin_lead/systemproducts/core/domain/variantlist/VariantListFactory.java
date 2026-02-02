package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.VariantsUuId;

import java.util.Set;

public class VariantListFactory {

    public static VariantListAggregate create(VariantListBusinessUuId bizId, Set<VariantsUuId> ids, Actor creator) {
        return new VariantListAggregate(
                VariantListId.of(0L),
                VariantListUuId.generate(),
                bizId,
                ids,
                AuditMetadata.create(creator)
        );
    }

    public static VariantListAggregate reconstitute(
            VariantListId id, VariantListUuId uuId, VariantListBusinessUuId bizId,
            Set<VariantsUuId> ids, AuditMetadata audit) {
        return new VariantListAggregate(id, uuId, bizId, ids, audit);
    }
}

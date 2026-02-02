package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.*;

import java.util.Set;

public class VariantFactory {

    public static VariantsAggregate create(
            VariantsBusinessUuId bizId, VariantsName name, Set<FeatureUuId> features, Actor creator) {
        return new VariantsAggregate(
                VariantsId.of(0L),
                VariantsUuId.generate(),
                bizId,
                name,
                features,
                AuditMetadata.create(creator)
        );
    }

    public static VariantsAggregate reconstitute(
            VariantsId id, VariantsUuId uuId, VariantsBusinessUuId bizId,
            VariantsName name, Set<FeatureUuId> features, AuditMetadata audit) {
        return new VariantsAggregate(id, uuId, bizId, name, features, audit);
    }
}

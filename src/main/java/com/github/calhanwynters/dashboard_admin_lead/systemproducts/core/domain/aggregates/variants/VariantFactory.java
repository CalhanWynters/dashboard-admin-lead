package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;

import java.util.Set;

public class VariantFactory {

    public static VariantsAggregateLEGACY create(VariantsBusinessUuId bizId, VariantsName name, Actor creator) {
        return VariantsAggregateLEGACY.create(VariantsUuId.generate(), bizId, name, creator);
    }

    public static VariantsAggregateLEGACY reconstitute(
            VariantsId id,
            VariantsUuId uuId,
            VariantsBusinessUuId bizId,
            VariantsName name,
            Set<FeatureUuId> features,
            ProductBooleans productBooleans, // Replaced boolean
            AuditMetadata audit) {

        return new VariantsAggregateLEGACY(id, uuId, bizId, name, features, productBooleans, audit);
    }
}

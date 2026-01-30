package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import java.util.HashSet;
import java.util.Set;

public class VariantFactory {

    public static VariantAggregate createNew(UuId businessId, Set<Feature> features) {
        validate(features);
        return new VariantAggregate(0, UuId.generate(), businessId, new HashSet<>(features));
    }

    public static VariantAggregate reconstitute(int primaryKey, UuId variantColId, UuId businessId, Set<Feature> features) {
        validate(features);
        return new VariantAggregate(primaryKey, variantColId, businessId, new HashSet<>(features));
    }

    private static void validate(Set<Feature> features) {
        if (features == null || features.isEmpty()) {
            throw new IllegalArgumentException("Variant Collection must have at least one feature.");
        }
    }
}

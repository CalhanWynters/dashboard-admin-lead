package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.variant;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.UuId;
import java.util.HashSet;
import java.util.Set;

public class VariantFactory {

    public static VariantAggregate createNew(UuId businessId, Set<Feature> features) {
        validate(features);
        return new VariantAggregate(0, UuId.generate(), businessId, new HashSet<>(features));
    }

    public static VariantAggregate reconstitute(int primaryKey, UuId variantId, UuId businessId, Set<Feature> features) {
        validate(features);
        return new VariantAggregate(primaryKey, variantId, businessId, new HashSet<>(features));
    }

    private static void validate(Set<Feature> features) {
        if (features == null || features.isEmpty()) {
            throw new IllegalArgumentException("Variant must have at least one feature.");
        }
    }
}

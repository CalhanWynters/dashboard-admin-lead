package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.variant;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.UuId;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class VariantCollectionFactory {

    public static VariantCollectionAggregate createNew(UuId businessId, Set<VariantAggregate> variants) {
        validate(variants);

        // Transform the set of objects into a set of IDs
        Set<UuId> variantIds = variants.stream()
                .map(VariantAggregate::getVariantId)
                .collect(Collectors.toSet());

        return new VariantCollectionAggregate(0, UuId.generate(), businessId, variantIds);
    }

    public static VariantCollectionAggregate reconstitute(int primaryKey, UuId variantColId, UuId businessId, Set<UuId> variantIds) {
        return new VariantCollectionAggregate(primaryKey, variantColId, businessId, new HashSet<>(variantIds));
    }

    private static void validate(Set<VariantAggregate> variants) {
        if (variants == null || variants.isEmpty()) {
            throw new IllegalArgumentException("Variant Collection must have at least one variant.");
        }
    }
}

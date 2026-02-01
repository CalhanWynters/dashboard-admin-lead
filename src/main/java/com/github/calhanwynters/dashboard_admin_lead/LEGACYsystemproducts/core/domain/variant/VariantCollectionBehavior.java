package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.variant;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.UuId;
import java.util.HashSet;
import java.util.Set;

public class VariantCollectionBehavior {

    private final VariantCollectionAggregate variantCollection;

    public VariantCollectionBehavior(VariantCollectionAggregate variantCollection) {
        this.variantCollection = variantCollection;
    }

    public VariantCollectionAggregate removeVariant(UuId variantId) {
        // Compare IDs instead of full objects
        if (!variantCollection.getVariantIds().contains(variantId)) {
            throw new IllegalArgumentException("Variant ID not found in the collection.");
        }

        Set<UuId> updatedIds = new HashSet<>(variantCollection.getVariantIds());
        updatedIds.remove(variantId);

        return VariantCollectionFactory.reconstitute(
                variantCollection.getPrimaryKey(),
                variantCollection.getVariantColId(),
                variantCollection.getBusinessId(),
                updatedIds
        );
    }

    public VariantCollectionAggregate addVariant(UuId variantId) {
        Set<UuId> updatedIds = new HashSet<>(variantCollection.getVariantIds());
        updatedIds.add(variantId);

        return VariantCollectionFactory.reconstitute(
                variantCollection.getPrimaryKey(),
                variantCollection.getVariantColId(),
                variantCollection.getBusinessId(),
                updatedIds
        );
    }

    public int size() {
        return variantCollection.getVariantIds().size();
    }

    public boolean contains(UuId variantId) {
        return variantCollection.getVariantIds().contains(variantId);
    }
}

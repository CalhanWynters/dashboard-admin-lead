package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant;

import java.util.HashSet;
import java.util.Set;

public class VariantColBehavior {

    private final VariantCollection collection;

    public VariantColBehavior(VariantCollection collection) {
        this.collection = collection;
    }

    public VariantCollection removeFeature(Feature feature) {
        if (!collection.getFeatures().contains(feature)) {
            throw new IllegalArgumentException("Feature not found in the collection.");
        }

        Set<Feature> updatedFeatures = new HashSet<>(collection.getFeatures());
        updatedFeatures.remove(feature);

        return VariantColFactory.reconstitute(
                collection.getPrimaryKey(),
                collection.getVariantColId(),
                collection.getBusinessId(),
                updatedFeatures
        );
    }

    public VariantCollection addFeature(Feature feature) {
        Set<Feature> updatedFeatures = new HashSet<>(collection.getFeatures());
        updatedFeatures.add(feature);

        return VariantColFactory.reconstitute(
                collection.getPrimaryKey(),
                collection.getVariantColId(),
                collection.getBusinessId(),
                updatedFeatures
        );
    }

    public int size() { return collection.getFeatures().size(); }
    public boolean contains(Feature feature) { return collection.getFeatures().contains(feature); }
}

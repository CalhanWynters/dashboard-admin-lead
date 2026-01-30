package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variant;

import java.util.HashSet;
import java.util.Set;

public class VariantBehavior {

    private final VariantAggregate collection;

    public VariantBehavior(VariantAggregate collection) {
        this.collection = collection;
    }

    public VariantAggregate removeFeature(Feature feature) {
        if (!collection.getFeatures().contains(feature)) {
            throw new IllegalArgumentException("Feature not found in the collection.");
        }

        Set<Feature> updatedFeatures = new HashSet<>(collection.getFeatures());
        updatedFeatures.remove(feature);

        return VariantFactory.reconstitute(
                collection.getPrimaryKey(),
                collection.getVariantId(),
                collection.getBusinessId(),
                updatedFeatures
        );
    }

    public VariantAggregate addFeature(Feature feature) {
        Set<Feature> updatedFeatures = new HashSet<>(collection.getFeatures());
        updatedFeatures.add(feature);

        return VariantFactory.reconstitute(
                collection.getPrimaryKey(),
                collection.getVariantId(),
                collection.getBusinessId(),
                updatedFeatures
        );
    }

    public int size() { return collection.getFeatures().size(); }
    public boolean contains(Feature feature) { return collection.getFeatures().contains(feature); }
}

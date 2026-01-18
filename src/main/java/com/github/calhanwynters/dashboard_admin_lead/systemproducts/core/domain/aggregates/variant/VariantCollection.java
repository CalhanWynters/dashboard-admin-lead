package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;

import java.util.HashSet;
import java.util.Set;

/**
 * A representation of a product variant, which is a collection of features.
 * Each variant has a unique identifier and a list of associated features.
 */
public class VariantCollection {

    private final int primaryKey; // Local database primary key
    private final UuId variantColId; // Unique identifier for the variant
    private final UuId businessId; // Unique identifier for the associated business
    private final Set<Feature> features; // Set to hold unique features

    /**
     * @param businessId Unique identifier for the business associated with this variant
     */
    public VariantCollection(int primaryKey, UuId businessId, Set<Feature> features) {
        this.primaryKey = primaryKey; // Assign the local integer primary key
        this.variantColId = UuId.generate(); // Generate a new unique identifier for the variant
        this.businessId = businessId; // Assign the business ID
        this.features = Set.copyOf(features); // Ensure features are immutable
        validateFeatures();
    }

    // ======================== Validation ========================================================

    private void validateFeatures() {
        if (features.isEmpty()) {
            throw new IllegalArgumentException("Variant must have at least one feature.");
        }
        // Additional feature validation logic can go here
    }

    // ======================== Behavioral Methods ================================================

    // Common Getters
    public UuId getVariantColId() {
        return variantColId;
    }
    public UuId getBusinessId() {return businessId;}
    public int getPrimaryKey() {
        return primaryKey;
    }

    // Get & Remove
    public Set<Feature> getFeatures() {
        return Set.copyOf(features); // Return a copy to maintain immutability
    }
    public VariantCollection removeFeature(Feature feature) {
        if (!features.contains(feature)) {
            throw new IllegalArgumentException("Feature not found in the collection.");
        }

        Set<Feature> updatedFeatures = new HashSet<>(features);
        updatedFeatures.remove(feature);

        return new VariantCollection(primaryKey, businessId, updatedFeatures);
    }

    // Class Specific
    public int size() {return features.size();}
    public boolean contains(Feature feature) {return features.contains(feature);}
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import java.util.Collections;
import java.util.Set;

/**
 * Represents the immutable state of a Variant Collection Aggregate.
 */
public class VariantCollection {
    private final int primaryKey;
    private final UuId variantColId;
    private final UuId businessId;
    private final Set<Feature> features;

    // Package-private: Enforces use of VariantColFactory
    VariantCollection(int primaryKey, UuId variantColId, UuId businessId, Set<Feature> features) {
        this.primaryKey = primaryKey;
        this.variantColId = variantColId;
        this.businessId = businessId;
        this.features = Collections.unmodifiableSet(features);
    }

    /**
     * Entry point for domain logic transitions.
     */
    public VariantColBehavior act() {
        return new VariantColBehavior(this);
    }

    public int getPrimaryKey() { return primaryKey; }
    public UuId getVariantColId() { return variantColId; }
    public UuId getBusinessId() { return businessId; }
    public Set<Feature> getFeatures() { return features; }
}

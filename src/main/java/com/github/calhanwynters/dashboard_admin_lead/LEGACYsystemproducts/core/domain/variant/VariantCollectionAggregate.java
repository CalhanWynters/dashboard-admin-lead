package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.variant;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.UuId;
import java.util.Collections;
import java.util.Set;

public class VariantCollectionAggregate {
    private final int primaryKey;
    private final UuId variantColId;
    private final UuId businessId;
    private final Set<UuId> variantIds; // Reference by ID only

    // Package-private: Enforced by VariantCollectionFactory
    VariantCollectionAggregate(int primaryKey, UuId variantColId, UuId businessId, Set<UuId> variantIds) {
        this.primaryKey = primaryKey;
        this.variantColId = variantColId;
        this.businessId = businessId;
        this.variantIds = Collections.unmodifiableSet(variantIds);
    }

    /**
     * Entry point for domain logic transitions.
     */
    public VariantCollectionBehavior act() {
        return new VariantCollectionBehavior(this);
    }

    // Getters
    public int getPrimaryKey() { return primaryKey; }
    public UuId getVariantColId() { return variantColId; }
    public UuId getBusinessId() { return businessId; }

    /**
     * Returns the set of Variant IDs associated with this collection.
     */
    public Set<UuId> getVariantIds() { return variantIds; }
}

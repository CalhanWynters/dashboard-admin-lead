package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.type;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import java.util.Collections;
import java.util.Set;

/**
 * Represents the immutable state of a Type Collection Aggregate.
 */
public class TypeCollectionAggregate {
    private final int primaryKey;
    private final UuId typeColId;
    private final UuId businessId;
    private final Set<Type> types;

    // Package-private: Creation is restricted to the Factory
    TypeCollectionAggregate(int primaryKey, UuId typeColId, UuId businessId, Set<Type> types) {
        this.primaryKey = primaryKey;
        this.typeColId = typeColId;
        this.businessId = businessId;
        this.types = Collections.unmodifiableSet(types);
    }

    /**
     * Entry point for domain logic (Fluent API).
     */
    public TypeColBehavior act() {
        return new TypeColBehavior(this);
    }

    public int getPrimaryKey() { return primaryKey; }
    public UuId getTypeColId() { return typeColId; }
    public UuId getBusinessId() { return businessId; }
    public Set<Type> getTypes() { return types; }
}

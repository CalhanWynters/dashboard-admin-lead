package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.type;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;

import java.util.HashSet;
import java.util.Set;

/**
 * A collection of Type objects, each with a unique identifier and associated data.
 */
@SuppressWarnings("ClassCanBeRecord") // Prefer to keep as class for Domain Aggregate role
public class TypeCollection {

    private final int primaryKey; // Local database primary key
    private final UuId typeColId; // Unique identifier for the collection
    private final UuId businessId; // Unique identifier for the associated business
    private final Set<Type> types; // Set to hold unique Type objects

    /**
     * @param businessId Unique identifier for the business associated with this collection
     */
    public TypeCollection(int primaryKey, UuId typeColId, UuId businessId, Set<Type> types) {
        this.primaryKey = primaryKey; // Assign the local integer primary key
        this.typeColId = UuId.generate(); // Assign the collection ID
        this.businessId = businessId; // Assign the business ID
        this.types = Set.copyOf(types); // Create an immutable copy of the set
        validateType(); // Perform validation on initialization
    }

    // ======================== Validation ========================================================

    private void validateType() {
        if (types.isEmpty()) {
            throw new IllegalArgumentException("Type Collection must have at least one type.");
        }
        // Additional validation logic can go here, if needed
    }

    // ======================== Behavioral Methods ================================================

    // Common Getters
    public UuId getTypeColId() {
        return typeColId;
    }
    public UuId getBusinessId() {
        return businessId;
    }
    public int getPrimaryKey() {
        return primaryKey;
    }

    // Get & Remove
    public Set<Type> getTypes() {
        return types; // Return the immutable set directly
    }
    public TypeCollection removeTypes(Type type) {
        if (!types.contains(type)) {
            throw new IllegalArgumentException("Type not found in the collection.");
        }

        Set<Type> updatedTypes = new HashSet<>(types);
        updatedTypes.remove(type); // Remove the specified type

        return new TypeCollection(primaryKey, typeColId, businessId, updatedTypes); // Expected 4 arguments but found 3
    }

    // Class Specific
    public int size() {
        return types.size();
    }
    public boolean contains(Type type) {
        return types.contains(type);
    }
}

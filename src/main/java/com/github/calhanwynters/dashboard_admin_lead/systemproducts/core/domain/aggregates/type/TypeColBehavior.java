package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.type;

import java.util.HashSet;
import java.util.Set;

public class TypeColBehavior {

    private final TypeCollection collection;

    public TypeColBehavior(TypeCollection collection) {
        this.collection = collection;
    }

    public TypeCollection removeType(Type type) {
        if (!collection.getTypes().contains(type)) {
            throw new IllegalArgumentException("Type not found in the collection.");
        }

        Set<Type> updatedTypes = new HashSet<>(collection.getTypes());
        updatedTypes.remove(type);

        return TypeColFactory.reconstitute(
                collection.getPrimaryKey(),
                collection.getTypeColId(),
                collection.getBusinessId(),
                updatedTypes
        );
    }

    public TypeCollection addType(Type type) {
        Set<Type> updatedTypes = new HashSet<>(collection.getTypes());
        updatedTypes.add(type);

        return TypeColFactory.reconstitute(
                collection.getPrimaryKey(),
                collection.getTypeColId(),
                collection.getBusinessId(),
                updatedTypes
        );
    }

    public int size() { return collection.getTypes().size(); }
    public boolean contains(Type type) { return collection.getTypes().contains(type); }
}

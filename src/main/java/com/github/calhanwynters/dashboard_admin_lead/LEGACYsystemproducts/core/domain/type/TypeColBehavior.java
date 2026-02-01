package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.type;

import java.util.HashSet;
import java.util.Set;

public class TypeColBehavior {

    private final TypeCollectionAggregate collection;

    public TypeColBehavior(TypeCollectionAggregate collection) {
        this.collection = collection;
    }

    public TypeCollectionAggregate removeType(Type type) {
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

    public TypeCollectionAggregate addType(Type type) {
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

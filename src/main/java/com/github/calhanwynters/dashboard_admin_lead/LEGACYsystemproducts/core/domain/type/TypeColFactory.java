package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.type;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.UuId;
import java.util.HashSet;
import java.util.Set;

public class TypeColFactory {

    public static TypeCollectionAggregate createNew(UuId businessId, Set<Type> types) {
        validate(types);
        return new TypeCollectionAggregate(0, UuId.generate(), businessId, new HashSet<>(types));
    }

    public static TypeCollectionAggregate reconstitute(int primaryKey, UuId typeColId, UuId businessId, Set<Type> types) {
        validate(types);
        return new TypeCollectionAggregate(primaryKey, typeColId, businessId, new HashSet<>(types));
    }

    private static void validate(Set<Type> types) {
        if (types == null || types.isEmpty()) {
            throw new IllegalArgumentException("Type Collection must have at least one type.");
        }
    }
}

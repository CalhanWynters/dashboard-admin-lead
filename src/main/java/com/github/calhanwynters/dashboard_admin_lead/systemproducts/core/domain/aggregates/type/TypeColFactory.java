package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.type;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import java.util.HashSet;
import java.util.Set;

public class TypeColFactory {

    public static TypeCollection createNew(UuId businessId, Set<Type> types) {
        validate(types);
        return new TypeCollection(0, UuId.generate(), businessId, new HashSet<>(types));
    }

    public static TypeCollection reconstitute(int primaryKey, UuId typeColId, UuId businessId, Set<Type> types) {
        validate(types);
        return new TypeCollection(primaryKey, typeColId, businessId, new HashSet<>(types));
    }

    private static void validate(Set<Type> types) {
        if (types == null || types.isEmpty()) {
            throw new IllegalArgumentException("Type Collection must have at least one type.");
        }
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.TypesUuId;
import java.util.Set;

public final class TypeListBehavior {

    private static final int MAX_TYPES = 50; // Example business constraint

    public static void ensureActive(boolean isDeleted) {
        if (isDeleted) {
            throw new IllegalStateException("Operation failed: TypeList is deleted.");
        }
    }

    public static void ensureCanAttach(Set<TypesUuId> currentTypes, TypesUuId typeUuId) {
        DomainGuard.notNull(typeUuId, "Type UUID");
        if (currentTypes.size() >= MAX_TYPES) {
            throw new IllegalStateException("TypeList has reached maximum capacity.");
        }
        if (currentTypes.contains(typeUuId)) {
            throw new IllegalArgumentException("Type is already attached to this list.");
        }
    }

    public static void ensureCanDetach(Set<TypesUuId> currentTypes, TypesUuId typeUuId) {
        DomainGuard.notNull(typeUuId, "Type UUID");
        if (!currentTypes.contains(typeUuId)) {
            throw new IllegalArgumentException("Type is not found in this list.");
        }
    }
}


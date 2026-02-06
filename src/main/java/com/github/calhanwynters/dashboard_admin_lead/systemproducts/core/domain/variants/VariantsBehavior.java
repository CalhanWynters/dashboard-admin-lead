package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.FeatureUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.*;
import java.util.Set;

public final class VariantsBehavior {

    private VariantsBehavior() {}

    public static void ensureActive(boolean deleted) {
        if (deleted) {
            throw new IllegalStateException("Operation failed: Variant is deleted.");
        }
    }

    public static VariantsName evaluateRename(VariantsName current, VariantsName next) {
        DomainGuard.notNull(next, "New Variant Name");
        if (next.equals(current)) {
            throw new IllegalArgumentException("New name must be different from current name.");
        }
        return next;
    }

    public static void ensureCanAssign(Set<FeatureUuId> current, FeatureUuId next) {
        DomainGuard.notNull(next, "Feature UUID");
        if (current.contains(next)) {
            throw new IllegalArgumentException("Feature is already assigned to this variant.");
        }
    }

    public static void ensureCanUnassign(Set<FeatureUuId> current, FeatureUuId target) {
        DomainGuard.notNull(target, "Feature UUID");
        if (!current.contains(target)) {
            throw new IllegalArgumentException("Feature is not found on this variant.");
        }
    }

    public static VariantsBusinessUuId evaluateBusinessIdChange(VariantsBusinessUuId current, VariantsBusinessUuId next) {
        DomainGuard.notNull(next, "New Business UUID");
        if (next.equals(current)) {
            throw new IllegalArgumentException("New Business ID must be different.");
        }
        return next;
    }
}

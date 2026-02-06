package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.*;

/**
 * Pure Behavioral Logic for Product Types.
 * Performs all invariant checks and delta detection without side effects.
 */
public final class TypesBehavior {

    private TypesBehavior() {
        // Prevent instantiation of utility class
    }

    public static void ensureActive(boolean isDeleted) {
        if (isDeleted) {
            throw new IllegalStateException("Operation failed: Product Type is deleted.");
        }
    }

    public static TypesName evaluateRename(TypesName current, TypesName next) {
        DomainGuard.notNull(next, "New Type Name");
        if (next.equals(current)) {
            throw new IllegalArgumentException("New name must be different from current name.");
        }
        return next;
    }

    public static void validateSpecs(TypesPhysicalSpecs specs) {
        DomainGuard.notNull(specs, "Physical Specs");
    }

    public static boolean detectDimensionChange(TypesPhysicalSpecs current, TypesPhysicalSpecs next) {
        DomainGuard.notNull(current, "Current Specs");
        DomainGuard.notNull(next, "Next Specs");
        return !next.value().dimensions().equals(current.value().dimensions());
    }

    public static boolean detectWeightShift(TypesPhysicalSpecs current, TypesPhysicalSpecs next) {
        DomainGuard.notNull(current, "Current Specs");
        DomainGuard.notNull(next, "Next Specs");
        return !next.value().weight().equals(current.value().weight());
    }
}

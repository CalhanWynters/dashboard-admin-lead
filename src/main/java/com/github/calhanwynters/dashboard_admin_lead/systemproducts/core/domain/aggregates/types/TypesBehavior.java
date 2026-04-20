package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

/**
 * Pure Behavioral Logic for Product Types.
 * Enforces SOC 2 Processing Integrity and Role-Based Access Control.
 */
public final class TypesBehavior {

    private TypesBehavior() {}

    /**
     * Standardized creation validation.
     */
    public static void validateCreation(TypesUuId uuId, TypesBusinessUuId bUuId, Actor actor) {
        BaseAggregateRoot.verifyLifecycleAuthority(actor);
        DomainGuard.notNull(uuId, "Type UUID");
        DomainGuard.notNull(bUuId, "Business UUID");
    }

    public static void verifyManagementAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify Type attributes.", "SEC-403", actor);
        }
    }

    /**
     * Specialized Rule: Types require ADMIN for lifecycle (stricter than Base).
     */
    public static void verifyLifecycleAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Lifecycle actions (Delete/Restore) are restricted to Administrators.", "SEC-001", actor);
        }
    }

    // --- DOMAIN-SPECIFIC EVALUATORS ---

    public static TypesName evaluateRename(TypesName current, TypesName next, Actor actor) {
        verifyManagementAuthority(actor);
        DomainGuard.notNull(next, "New Type Name");
        if (next.equals(current)) {
            throw new IllegalArgumentException("New name must be different from current name.");
        }
        return next;
    }

    public static TypesRegion evaluateRegionTransition(TypesRegion current, TypesRegion target, Actor actor) {
        DomainGuard.notNull(target, "Target Types Region");

        // Example Authorization: Only Managers can change product regions
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Insufficient privileges to change types region.", "SEC-403", actor);
        }

        // Invariants: Ensure the region isn't changing to the same value unnecessarily (Optional)
        DomainGuard.ensure(
                !current.equals(target),
                "Type is already assigned to region: %s".formatted(target.value()),
                "VAL-016", "STATE_VIOLATION"
        );

        return target;
    }

    public static void validateSpecs(TypesPhysicalSpecs specs, Actor actor) {
        verifyManagementAuthority(actor);
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

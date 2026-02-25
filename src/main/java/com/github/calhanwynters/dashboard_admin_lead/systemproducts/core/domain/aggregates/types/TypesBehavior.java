package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

/**
 * Pure Behavioral Logic for Product Types.
 * Enforces SOC 2 Processing Integrity and Role-Based Access Control.
 */
public final class TypesBehavior {

    private TypesBehavior() {}

    public static void verifyCreationAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Type creation requires Manager or Admin roles.", "SEC-403", actor);
        }
    }

    public static void verifyManagementAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify Type attributes.", "SEC-403", actor);
        }
    }

    public static void verifyLifecycleAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Lifecycle actions (Delete/Restore) are restricted to Administrators.", "SEC-001", actor);
        }
    }

    public static void ensureActive(boolean isDeleted) {
        if (isDeleted) {
            throw new IllegalStateException("Operation failed: Product Type is deleted.");
        }
    }

    /**
     * SOC 2: Ensures only authorized roles can trigger a manual data synchronization.
     */
    public static void verifySyncAuthority(Actor actor) {
        // Typically restricted to Admin/Manager to prevent unauthorized data exfiltration
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException(
                    "Data synchronization requires Manager or Admin roles.",
                    "SEC-403", actor);
        }
    }

    public static TypesName evaluateRename(TypesName current, TypesName next, Actor actor) {
        verifyManagementAuthority(actor);
        DomainGuard.notNull(next, "New Type Name");
        if (next.equals(current)) {
            throw new IllegalArgumentException("New name must be different from current name.");
        }
        return next;
    }

    public static TypesBusinessUuId evaluateBusinessIdChange(TypesBusinessUuId currentId,
                                                             TypesBusinessUuId newId, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Business ID modification is restricted to Administrators.", "SEC-401", actor);
        }

        DomainGuard.notNull(newId, "New Business UUID");
        if (currentId.equals(newId)) {
            throw new IllegalArgumentException("The new Business ID must be different from the current one.");
        }
        return newId;
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

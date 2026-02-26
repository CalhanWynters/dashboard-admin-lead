package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.StatusEnums;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;

/**
 * Pure Behavioral Logic for Product Aggregate.
 * Enforces SOC 2 Processing Integrity and Role-Based Access Control.
 */
public final class ProductBehavior {

    private ProductBehavior() {}

    // --- NEW: LIFECYCLE & ACTIVITY GUARDS ---

    /**
     * SOC 2: Ensures no state modifications occur on a soft-deleted record.
     */
    public static void ensureActive(boolean isSoftDeleted) {
        DomainGuard.ensure(
                !isSoftDeleted,
                "Domain Violation: The product is soft-deleted and cannot be modified.",
                "VAL-018", "STATE_LOCKED"
        );
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

    /**
     * SOC 2: Restricts high-impact lifecycle changes (Archive/Delete/Restore).
     */
    public static void verifyLifecycleAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN) && !actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException(
                    "Lifecycle management (Archive/Delete/Restore) requires Manager or Admin roles.",
                    "SEC-001", actor);
        }
    }

    /**
     * SOC 2: Ensures version increments are restricted to Managers or Admins.
     */
    public static void verifyVersionUpdateAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException(
                    "Manual version increments require Manager or Admin roles.",
                    "SEC-403", actor);
        }
    }

    // --- EXISTING AUTHORITY CHECKS ---

    /**
     * Validates authority and state machine rules for status changes.
     */
    public static ProductStatus evaluateStatusTransition(ProductStatus current, ProductStatus target, Actor actor) {
        DomainGuard.notNull(target, "Target Product Status");
        verifyStatusTransitionAuthority(actor, target);
        validateStatusTransition(current, target);

        if (current.equals(target)) {
            throw new IllegalArgumentException("Product is already in status: " + target.value());
        }
        return target;
    }

    public static void verifyCreationAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Product creation requires Manager or Admin roles.", "SEC-403", actor);
        }
    }

    public static void verifyStructuralChangeAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Structural product changes (Price/Type lists) are restricted to Administrators.", "SEC-001", actor);
        }
    }

    public static void verifyManifestUpdateAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can update product manifest details.", "SEC-403", actor);
        }
    }

    public static void verifyStatusTransitionAuthority(Actor actor, ProductStatus target) {
        if (target == ProductStatus.ACTIVE && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Only Administrators can activate products for production.", "SEC-001", actor);
        }

        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Insufficient privileges for status transition.", "SEC-403", actor);
        }
    }

    // --- EXISTING VALIDATION & UTILS ---

    public static void validateStatusTransition(ProductStatus current, ProductStatus next) {
        DomainGuard.ensure(
                current.canTransitionTo(next),
                "Illegal transition from %s to %s.".formatted(current.value(), next.value()),
                "VAL-016", "STATE_VIOLATION"
        );
    }

    public static void validateComposition(ProductAggregateRoot product) {
        DomainGuard.ensure(
                new ProductCompositionSpecification().isSatisfiedBy(product),
                "Invalid Product Type: Standard products (with TypeList) cannot have local Price/Specs. " +
                        "Bespoke products (without TypeList) require both Price and Specs.",
                "VAL-016", "INVARIANT_VIOLATION"
        );
    }

    public static void ensureDependencyResolution(String type, boolean exists, Actor actor) {
        if (actor.equals(Actor.SYSTEM)) return;

        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Insufficient privileges to record missing dependencies.", "SEC-403", actor);
        }

        DomainGuard.ensure(
                exists,
                "Critical dependency [%s] is missing. Product must enter a safe state.".formatted(type),
                "VAL-017", "DEPENDENCY_MISSING"
        );
    }

    public static ProductVersion incrementVersion(ProductVersion current) {
        return new ProductVersion(current.value().next());
    }

    public static void validateManifest(ProductManifest manifest) {
        DomainGuard.notNull(manifest, "Product Manifest");
    }

    public static void validatePhysicalSpecs(ProductPhysicalSpecs specs) {
        DomainGuard.notNull(specs, "Physical Specs");
    }

    public static ProductBusinessUuId evaluateBusinessIdChange(ProductBusinessUuId currentId,
                                                               ProductBusinessUuId newId, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Business ID modification is restricted to Administrators.", "SEC-401", actor);
        }

        DomainGuard.notNull(newId, "New Business UUID");
        if (currentId.equals(newId)) {
            throw new IllegalArgumentException("The new Business ID must be different from the current one.");
        }
        return newId;
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;

/**
 * Pure Behavioral Logic for Product Aggregate.
 * Enforces SOC 2 Processing Integrity and Role-Based Access Control.
 */
public final class ProductBehavior {

    private ProductBehavior() {}

    public static void verifyCreationAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Product creation requires Manager or Admin roles.", "SEC-403", actor);
        }
    }

    public static void verifyStructuralChangeAuthority(Actor actor) {
        // SOC 2: Changing PriceLists or TypeLists is a high-risk structural change
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
        // SOC 2: Activating a product for public view is higher risk than drafting
        if (target == ProductStatus.ACTIVE && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Only Administrators can activate products for production.", "SEC-001", actor);
        }

        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Insufficient privileges for status transition.", "SEC-403", actor);
        }
    }

    public static void validateStatusTransition(ProductStatus current, ProductStatus next) {
        DomainGuard.ensure(
                current.canTransitionTo(next),
                "Illegal transition from %s to %s.".formatted(current, next),
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
        // SOC 2: Allow SYSTEM actor for self-healing, otherwise require Manager
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
}

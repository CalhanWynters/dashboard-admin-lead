package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;

public final class ProductBehavior {

    private ProductBehavior() {}

    public static void validateCreation(ProductUuId uuId, ProductBusinessUuId bUuId, Actor actor) {
        BaseAggregateRoot.verifyLifecycleAuthority(actor);
        DomainGuard.notNull(uuId, "Product UUID");
        DomainGuard.notNull(bUuId, "Business UUID");
    }

    // --- AUTHORIZATION GATES ---

    public static void verifyManifestUpdateAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can update product manifest/details.", "SEC-403", actor);
        }
    }

    public static void verifyStructuralChangeAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Structural changes (Pricing/Types) restricted to Administrators.", "SEC-001", actor);
        }
    }

    // --- VALIDATION & INVARIANTS ---

    public static void validateManifest(ProductManifest manifest) {
        DomainGuard.notNull(manifest, "Product Manifest");
    }

    public static void validatePhysicalSpecs(ProductPhysicalSpecs specs) {
        DomainGuard.notNull(specs, "Physical Specs");
    }

    public static void validateComposition(ProductAggregate product) {
        DomainGuard.ensure(
                new ProductCompositionSpecification().isSatisfiedBy(product),
                "Composition Violation: Standard products cannot have local Price/Specs.",
                "VAL-016", "INVARIANT_VIOLATION"
        );
    }

    // --- STATUS & DEPENDENCIES ---

    public static ProductStatus evaluateStatusTransition(ProductStatus current, ProductStatus target, Actor actor) {
        DomainGuard.notNull(target, "Target Product Status");

        if (target == ProductStatus.ACTIVE && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Only Administrators can activate products.", "SEC-001", actor);
        }

        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Insufficient privileges for status transition.", "SEC-403", actor);
        }

        DomainGuard.ensure(
                current.canTransitionTo(target),
                "Illegal transition from %s to %s.".formatted(current.value(), target.value()),
                "VAL-016", "STATE_VIOLATION"
        );

        return target;
    }

    public static void ensureDependencyResolution(String type, boolean exists, Actor actor) {
        // SOC 2: Allow SYSTEM to record missing dependencies without role check
        if (Actor.SYSTEM.equals(actor)) return;

        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Insufficient privileges to record missing dependencies.", "SEC-403", actor);
        }
    }

    // --- UTILS ---

    public static ProductVersion incrementVersion(ProductVersion current) {
        return new ProductVersion(current.value().next());
    }

    public static ProductThumbnailUrl evaluateUrlUpdate(ProductThumbnailUrl current, ProductThumbnailUrl next, Actor actor) {
        verifyManifestUpdateAuthority(actor);
        DomainGuard.notNull(next, "New Thumbnail URL");
        return next;
    }

    public static ProductRegion evaluateRegionTransition(ProductRegion current, ProductRegion target, Actor actor) {
        DomainGuard.notNull(target, "Target Product Region");

        // Example Authorization: Only Managers can change product regions
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Insufficient privileges to change product region.", "SEC-403", actor);
        }

        // Invariants: Ensure the region isn't changing to the same value unnecessarily (Optional)
        DomainGuard.ensure(
                !current.equals(target),
                "Product is already assigned to region: %s".formatted(target.value()),
                "VAL-016", "STATE_VIOLATION"
        );

        return target;
    }
}

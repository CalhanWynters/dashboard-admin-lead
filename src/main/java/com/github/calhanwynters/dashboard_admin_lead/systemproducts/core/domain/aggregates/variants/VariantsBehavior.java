package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;
import java.util.Set;

/**
 * Pure Behavioral Logic for Variants.
 * Enforces SOC 2 Role-Based Access Control and Data Integrity.
 */
public final class VariantsBehavior {

    private VariantsBehavior() {}

    /**
     * Standardized creation validation.
     */
    public static void validateCreation(VariantsUuId uuId, VariantsBusinessUuId bUuId, Actor actor) {
        BaseAggregateRoot.verifyLifecycleAuthority(actor);
        DomainGuard.notNull(uuId, "Variant UUID");
        DomainGuard.notNull(bUuId, "Business UUID");
    }

    public static void verifyManagementAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify Variant attributes.", "SEC-403", actor);
        }
    }

    /**
     * Specialized Rule: Variants require ADMIN for lifecycle (stricter than Base).
     */
    public static void verifyLifecycleAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Variant lifecycle actions require Administrator privileges.", "SEC-001", actor);
        }
    }

    // --- DOMAIN-SPECIFIC EVALUATORS ---

    public static VariantsName evaluateRename(VariantsName current, VariantsName next, Actor actor) {
        verifyManagementAuthority(actor);
        DomainGuard.notNull(next, "New Variant Name");
        if (next.equals(current)) {
            throw new IllegalArgumentException("New name must be different from current name.");
        }
        return next;
    }

    // --- FEATURE ASSIGNMENT INVARIANTS ---

    public static void ensureCanAssign(Set<FeatureUuId> current, FeatureUuId next, Actor actor) {
        verifyManagementAuthority(actor);
        DomainGuard.notNull(next, "Feature UUID");
        if (current.contains(next)) {
            throw new IllegalArgumentException("Feature is already assigned to this variant.");
        }
    }

    public static void ensureCanUnassign(Set<FeatureUuId> current, FeatureUuId target, Actor actor) {
        verifyManagementAuthority(actor);
        DomainGuard.notNull(target, "Feature UUID");
        if (!current.contains(target)) {
            throw new IllegalArgumentException("Feature is not found on this variant.");
        }
    }
}


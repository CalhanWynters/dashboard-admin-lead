package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

/**
 * Pure Behavioral Logic for Features.
 * Optimized for SOC 2 Compliance via Role-Based Access Control (RBAC).
 */
public final class FeaturesBehavior {

    private FeaturesBehavior() {}

    public record DetailsPatch(FeatureName name, FeatureLabel tag) {}

    /**
     * Logic for creating a new feature.
     */
    public static void validateCreation(FeatureUuId uuId, FeatureBusinessUuId bUuId,
                                        FeatureName name, FeatureLabel tag, Actor actor) {
        // 1. Authority Check
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Feature creation requires Manager or Admin roles.", "SEC-403", actor);
        }

        // 2. Invariant Checks
        DomainGuard.notNull(uuId, "Feature UUID");
        DomainGuard.notNull(bUuId, "Business UUID");
        DomainGuard.notNull(name, "Feature Name");
        DomainGuard.notNull(tag, "Compatibility Tag");
    }

    /**
     * Logic for updating details.
     */
    public static DetailsPatch evaluateUpdate(FeatureName newName, FeatureLabel newTag, Actor actor) {
        // 1. Authority Check
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Insufficient privileges to update feature details.", "SEC-403", actor);
        }

        // 2. Invariant Checks
        DomainGuard.notNull(newName, "New Feature Name");
        DomainGuard.notNull(newTag, "New Compatibility Tag");
        return new DetailsPatch(newName, newTag);
    }

    /**
     * Logic for changing the Business ID.
     */
    public static FeatureBusinessUuId evaluateBusinessIdChange(FeatureBusinessUuId currentId,
                                                               FeatureBusinessUuId newId, Actor actor) {
        // 1. Authority Check (Restricted to Admins)
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Business ID modification is restricted to Administrators.", "SEC-401", actor);
        }

        // 2. Invariant Checks
        DomainGuard.notNull(newId, "New Business UUID");
        if (currentId.equals(newId)) {
            throw new IllegalArgumentException("The new Business ID must be different from the current one.");
        }
        return newId;
    }

    /**
     * Logic for Soft Deletion.
     */
    public static void verifyDeletable(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Deletion requires Manager or Admin authority.", "SEC-403", actor);
        }
        // Additional business rules (e.g., check links) would go here
    }

    /**
     * Logic for Restoration.
     */
    public static void verifyRestorable(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Only Administrators can restore deleted features.", "SEC-403", actor);
        }
    }

    /**
     * Validates a tag change.
     */
    public static FeatureLabel evaluateCompatibilityChange(FeatureLabel newTag,
                                                           FeatureLabel currentTag, Actor actor) {
        // 1. Authority Check
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify compatibility tags.", "SEC-403", actor);
        }

        // 2. Invariant Checks
        DomainGuard.notNull(newTag, "New Compatibility Tag");
        if (newTag.equals(currentTag)) {
            throw new IllegalArgumentException("The new tag is identical to the current tag.");
        }
        return newTag;
    }

    /**
     * SOC 2 Control: Hard deletes are restricted to ADMINS only.
     */
    public static void verifyHardDeleteAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Only Administrators can perform hard deletes.", "SEC-001", actor);
        }
    }
}

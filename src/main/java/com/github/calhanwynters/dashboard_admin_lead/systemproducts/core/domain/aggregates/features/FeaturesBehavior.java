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

    // --- NEW: LIFECYCLE & ACTIVITY GUARDS ---

    /**
     * SOC 2: Ensures no state modifications occur on a soft-deleted feature.
     */
    public static void ensureActive(boolean isSoftDeleted) {
        DomainGuard.ensure(
                !isSoftDeleted,
                "Domain Violation: The feature is soft-deleted and cannot be modified.",
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
     * SOC 2: Standardizes lifecycle authority (Archive/Delete/Restore).
     */
    public static void verifyLifecycleAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException(
                    "Lifecycle management (Archive/Delete/Restore) requires Manager or Admin roles.",
                    "SEC-403", actor);
        }
    }

    // --- AUTHORITY CHECKS ---

    public static void validateCreation(FeatureUuId uuId, FeatureBusinessUuId bUuId,
                                        FeatureName name, FeatureLabel tag, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Feature creation requires Manager or Admin roles.", "SEC-403", actor);
        }

        DomainGuard.notNull(uuId, "Feature UUID");
        DomainGuard.notNull(bUuId, "Business UUID");
        DomainGuard.notNull(name, "Feature Name");
        DomainGuard.notNull(tag, "Compatibility Tag");
    }

    public static FeatureName evaluateFeatureNameUpdate(FeatureName newName, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Insufficient privileges to update the feature name.", "SEC-403", actor);
        }

        DomainGuard.notNull(newName, "New Feature Name");
        return newName;
    }

    public static FeatureLabel evaluateCompatibilityTagUpdate(FeatureLabel newTag, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify compatibility tags.", "SEC-403", actor);
        }

        DomainGuard.notNull(newTag, "New Compatibility Tag");
        return newTag;
    }


    public static FeatureBusinessUuId evaluateBusinessIdChange(FeatureBusinessUuId currentId,
                                                               FeatureBusinessUuId newId, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Business ID modification is restricted to Administrators.", "SEC-401", actor);
        }

        DomainGuard.notNull(newId, "New Business UUID");
        if (currentId.equals(newId)) {
            throw new IllegalArgumentException("The new Business ID must be different from the current one.");
        }
        return newId;
    }

    public static void verifyDeletable(Actor actor) {
        verifyLifecycleAuthority(actor);
    }

    public static void verifyRestorable(Actor actor) {
        // Keeping your previous restriction of Admin-only for restoration
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Only Administrators can restore deleted features.", "SEC-403", actor);
        }
    }

    public static FeatureLabel evaluateCompatibilityChange(FeatureLabel newTag,
                                                           FeatureLabel currentTag, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify compatibility tags.", "SEC-403", actor);
        }

        DomainGuard.notNull(newTag, "New Compatibility Tag");
        if (newTag.equals(currentTag)) {
            throw new IllegalArgumentException("The new tag is identical to the current tag.");
        }
        return newTag;
    }

    public static void verifyHardDeleteAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Only Administrators can perform hard deletes.", "SEC-001", actor);
        }
    }
}

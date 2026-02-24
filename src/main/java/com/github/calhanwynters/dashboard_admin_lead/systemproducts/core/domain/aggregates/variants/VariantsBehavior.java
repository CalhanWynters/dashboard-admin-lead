package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
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

    public static void verifyCreationAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Variant creation requires Manager or Admin roles.", "SEC-403", actor);
        }
    }

    public static void verifyManagementAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify Variant attributes.", "SEC-403", actor);
        }
    }

    public static void verifyLifecycleAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Variant lifecycle actions (Delete/Restore) require Administrator privileges.", "SEC-001", actor);
        }
    }

    public static void ensureActive(boolean deleted) {
        if (deleted) {
            throw new IllegalStateException("Operation failed: Variant is deleted.");
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

    public static VariantsName evaluateRename(VariantsName current, VariantsName next, Actor actor) {
        verifyManagementAuthority(actor);
        DomainGuard.notNull(next, "New Variant Name");
        if (next.equals(current)) {
            throw new IllegalArgumentException("New name must be different from current name.");
        }
        return next;
    }

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

    public static VariantsBusinessUuId evaluateBusinessIdChange(VariantsBusinessUuId current, VariantsBusinessUuId next, Actor actor) {
        // SOC 2: Changing business identities is a high-risk traceability change
        verifyLifecycleAuthority(actor);
        DomainGuard.notNull(next, "New Business UUID");
        if (next.equals(current)) {
            throw new IllegalArgumentException("New Business ID must be different.");
        }
        return next;
    }
}

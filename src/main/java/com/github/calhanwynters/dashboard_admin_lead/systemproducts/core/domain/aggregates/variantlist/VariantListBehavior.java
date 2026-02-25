package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;
import java.util.Set;

/**
 * Pure Behavioral Logic for VariantList.
 * Enforces SOC 2 Role-Based Access Control and Membership Invariants.
 */
public final class VariantListBehavior {

    private static final int MAX_VARIANTS = 100;

    private VariantListBehavior() {}

    public static void verifyCreationAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("VariantList creation requires Manager or Admin roles.", "SEC-403", actor);
        }
    }

    public static void verifyMembershipAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify VariantList memberships.", "SEC-403", actor);
        }
    }

    public static void verifyLifecycleAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("VariantList lifecycle actions (Delete/Restore) are restricted to Administrators.", "SEC-001", actor);
        }
    }

    public static void ensureActive(boolean deleted) {
        if (deleted) {
            throw new IllegalStateException("Operation failed: VariantList is deleted.");
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

    public static VariantListDomainWrapper.VariantListBusinessUuId evaluateBusinessIdChange(VariantListBusinessUuId currentId,
                                                                                            VariantListBusinessUuId newId, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Business ID modification is restricted to Administrators.", "SEC-401", actor);
        }

        DomainGuard.notNull(newId, "New Business UUID");
        if (currentId.equals(newId)) {
            throw new IllegalArgumentException("The new Business ID must be different from the current one.");
        }
        return newId;
    }

    public static void ensureCanAttach(Set<VariantsUuId> current, VariantsUuId next, Actor actor) {
        verifyMembershipAuthority(actor);
        DomainGuard.notNull(next, "Variant UUID");
        if (current.size() >= MAX_VARIANTS) {
            throw new IllegalStateException("VariantList limit reached (%d)".formatted(MAX_VARIANTS));
        }
        if (current.contains(next)) {
            throw new IllegalArgumentException("Variant already attached to this list.");
        }
    }

    public static void ensureCanDetach(Set<VariantsUuId> current, VariantsUuId target, Actor actor) {
        verifyMembershipAuthority(actor);
        DomainGuard.notNull(target, "Variant UUID");
        if (!current.contains(target)) {
            throw new IllegalArgumentException("Variant not found in this list.");
        }
    }

    public static void ensureCanReorder(Set<VariantsUuId> current, Actor actor) {
        verifyMembershipAuthority(actor);
        if (current.size() < 2) {
            throw new IllegalStateException("Cannot reorder a list with fewer than 2 variants.");
        }
    }
}

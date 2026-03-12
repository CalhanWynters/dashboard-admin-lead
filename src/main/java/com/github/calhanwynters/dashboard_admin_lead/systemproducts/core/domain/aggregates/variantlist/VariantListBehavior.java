package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
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

    /**
     * Standardized creation validation.
     */
    public static void validateCreation(VariantListUuId uuId, VariantListBusinessUuId bUuId, Actor actor) {
        BaseAggregateRoot.verifyLifecycleAuthority(actor);
        DomainGuard.notNull(uuId, "VariantList UUID");
        DomainGuard.notNull(bUuId, "Business UUID");
    }

    public static void verifyMembershipAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify VariantList memberships.", "SEC-403", actor);
        }
    }

    /**
     * Specialized Rule: VariantLists require ADMIN for lifecycle (stricter than Base).
     */
    public static void verifyLifecycleAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Lifecycle actions restricted to Administrators.", "SEC-001", actor);
        }
    }

    // --- COLLECTION INTEGRITY ---

    public static void ensureCanAttach(Set<VariantsUuId> current, VariantsUuId next, Actor actor) {
        verifyMembershipAuthority(actor);
        DomainGuard.notNull(next, "Variant UUID");
        if (current.size() >= MAX_VARIANTS) {
            throw new IllegalStateException("VariantList limit reached (%d)".formatted(MAX_VARIANTS));
        }
        if (current.contains(next)) {
            throw new IllegalArgumentException("Variant is already in this list.");
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

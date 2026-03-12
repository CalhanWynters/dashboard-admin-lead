package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;
import java.util.Set;

/**
 * Pure Behavioral Logic for TypeList management.
 * Enforces SOC 2 Processing Integrity and Role-Based Access Control.
 */
public final class TypeListBehavior {

    private static final int MAX_TYPES = 50;

    private TypeListBehavior() {}

    /**
     * Standardized creation validation.
     */
    public static void validateCreation(TypeListUuId uuId, TypeListBusinessUuId bUuId, Actor actor) {
        BaseAggregateRoot.verifyLifecycleAuthority(actor);
        DomainGuard.notNull(uuId, "TypeList UUID");
        DomainGuard.notNull(bUuId, "Business UUID");
    }

    /**
     * Membership logic - Keep because it has a specific ROLE_MANAGER requirement.
     */
    public static void verifyMembershipAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify TypeList memberships.", "SEC-403", actor);
        }
    }

    /**
     * STATED RULE: TypeList requires ADMIN for lifecycle (stricter than Base).
     * If you prefer the Base rule (Manager/Admin), delete this and use the Base.
     */
    public static void verifyLifecycleAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Lifecycle actions (Delete/Restore) are restricted to Administrators.", "SEC-001", actor);
        }
    }

    // --- COLLECTION INTEGRITY ---

    public static void ensureCanAttach(Set<TypesUuId> currentTypes, TypesUuId typeUuId, Actor actor) {
        verifyMembershipAuthority(actor);
        DomainGuard.notNull(typeUuId, "Type UUID");
        if (currentTypes.size() >= MAX_TYPES) {
            throw new IllegalStateException("TypeList has reached maximum capacity (%d).".formatted(MAX_TYPES));
        }
        if (currentTypes.contains(typeUuId)) {
            throw new IllegalArgumentException("Type is already attached to this list.");
        }
    }

    public static void ensureCanDetach(Set<TypesUuId> currentTypes, TypesUuId typeUuId, Actor actor) {
        verifyMembershipAuthority(actor);
        DomainGuard.notNull(typeUuId, "Type UUID");
        if (!currentTypes.contains(typeUuId)) {
            throw new IllegalArgumentException("Type is not found in this list.");
        }
    }
}

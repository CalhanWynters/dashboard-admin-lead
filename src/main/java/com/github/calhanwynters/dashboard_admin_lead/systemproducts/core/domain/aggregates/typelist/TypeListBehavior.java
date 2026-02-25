package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
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

    public static void verifyCreationAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("TypeList creation requires Manager or Admin roles.", "SEC-403", actor);
        }
    }

    public static void verifyMembershipAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify TypeList memberships.", "SEC-403", actor);
        }
    }

    public static void verifyLifecycleAuthority(Actor actor) {
        // SOC 2: Deleting or restoring structural lists requires Administrator privileges
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Lifecycle actions (Delete/Restore) are restricted to Administrators.", "SEC-001", actor);
        }
    }

    public static void ensureActive(boolean isDeleted) {
        if (isDeleted) {
            throw new IllegalStateException("Operation failed: TypeList is deleted.");
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

    public static TypeListBusinessUuId evaluateBusinessIdChange(TypeListBusinessUuId currentId,
                                                                TypeListBusinessUuId newId, Actor actor) {
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

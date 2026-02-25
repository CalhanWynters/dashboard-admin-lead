package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

/**
 * Pure Behavioral Logic for Images.
 * Enforces SOC 2 Processing Integrity and Role-Based Access Control.
 */
public final class ImagesBehavior {

    private ImagesBehavior() {}

    public record MetadataPatch(ImageName name, ImageDescription description) {}

    // --- NEW: LIFECYCLE & ACTIVITY GUARDS ---

    /**
     * SOC 2: Ensures no state modifications occur on a soft-deleted image.
     */
    public static void ensureActive(boolean isSoftDeleted) {
        DomainGuard.ensure(
                !isSoftDeleted,
                "Domain Violation: The image is soft-deleted and cannot be modified.",
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

    public static void verifyCreationAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Image upload requires Manager or Admin roles.", "SEC-403", actor);
        }
    }

    public static MetadataPatch evaluateMetadataUpdate(ImageName name, ImageDescription description, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can update image metadata.", "SEC-403", actor);
        }

        DomainGuard.notNull(name, "Image Name");
        DomainGuard.notNull(description, "Image Description");
        return new MetadataPatch(name, description);
    }

    public static ImageDescription evaluateAltTextChange(ImageDescription current, ImageDescription next, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify alt text.", "SEC-403", actor);
        }

        DomainGuard.notNull(next, "New Image Description");
        if (next.equals(current)) {
            throw new IllegalArgumentException("New alt text is identical to the current one.");
        }
        return next;
    }

    public static ImagesBusinessUuId evaluateBusinessIdChange(ImagesBusinessUuId currentId,
                                                              ImagesBusinessUuId newId, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Business ID modification is restricted to Administrators.", "SEC-401", actor);
        }

        DomainGuard.notNull(newId, "New Business UUID");
        if (currentId.equals(newId)) {
            throw new IllegalArgumentException("The new Business ID must be different from the current one.");
        }
        return newId;
    }

    /**
     * @deprecated Use verifyLifecycleAuthority for consistency across aggregates.
     * Kept for logic specific to "already archived" state.
     */
    public static void verifyArchivable(boolean alreadyArchived, Actor actor) {
        verifyLifecycleAuthority(actor);
        if (alreadyArchived) {
            throw new IllegalStateException("Image is already archived.");
        }
    }

    public static void verifyDeletable(Actor actor) {
        verifyLifecycleAuthority(actor);
    }

    public static void verifyHardDeleteAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Hard deletes are restricted to Administrators.", "SEC-001", actor);
        }
    }

    public static void verifyReferenceAuthority(Actor actor) {
        if (Actor.SYSTEM.equals(actor)) return;

        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Insufficient privileges to record image references.", "SEC-403", actor);
        }
    }
}

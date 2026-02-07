package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.common.Actor.SYSTEM;
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

    public static void verifyArchivable(boolean alreadyArchived, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can archive images.", "SEC-403", actor);
        }
        if (alreadyArchived) {
            throw new IllegalStateException("Image is already archived.");
        }
    }

    public static void verifyDeletable(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Deletion requires Manager or Admin authority.", "SEC-403", actor);
        }
    }

    public static void verifyHardDeleteAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Hard deletes are restricted to Administrators.", "SEC-001", actor);
        }
    }

    public static void verifyReferenceAuthority(Actor actor) {
        // System actor often records references; ensure it has the role or identity
        if (Actor.SYSTEM.equals(actor)) return; // Use Actor.SYSTEM

        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Insufficient privileges to record image references.", "SEC-403", actor);
        }
    }
}

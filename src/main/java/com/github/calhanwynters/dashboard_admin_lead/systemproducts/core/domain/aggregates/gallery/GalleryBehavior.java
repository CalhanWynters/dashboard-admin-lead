package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

/**
 * Pure Behavioral Logic for Gallery management.
 * Enforces SOC 2 Processing Integrity and Role-Based Access Control.
 */
public final class GalleryBehavior {

    private static final int MAX_GALLERY_SIZE = 50;

    private GalleryBehavior() {}

    public static void verifyCreationAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Gallery creation requires Manager or Admin roles.", "SEC-403", actor);
        }
    }

    public static ImageUuId evaluateImageAddition(ImageUuId imageUuId, int currentSize, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify gallery content.", "SEC-403", actor);
        }

        DomainGuard.notNull(imageUuId, "Image UUID");
        if (currentSize >= MAX_GALLERY_SIZE) {
            throw new IllegalStateException("Gallery limit reached (%d)".formatted(MAX_GALLERY_SIZE));
        }
        return imageUuId;
    }

    public static ImageUuId evaluateImageRemoval(ImageUuId imageUuId, boolean containsImage, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can remove gallery content.", "SEC-403", actor);
        }

        DomainGuard.notNull(imageUuId, "Image UUID");
        if (!containsImage) {
            throw new IllegalArgumentException("Image not found in this gallery.");
        }
        return imageUuId;
    }

    public static boolean evaluatePublicityChange(boolean currentStatus, boolean newStatus, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can toggle gallery visibility.", "SEC-403", actor);
        }

        if (currentStatus == newStatus) {
            throw new IllegalArgumentException("Gallery is already " + (currentStatus ? "public" : "private"));
        }
        return newStatus;
    }

    public static void verifyReorderable(int currentSize, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Unauthorized reorder attempt.", "SEC-403", actor);
        }
        if (currentSize <= 1) {
            throw new IllegalStateException("Cannot reorder a gallery with fewer than 2 images.");
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

    public static void verifyRestorable(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Only Administrators can restore galleries.", "SEC-403", actor);
        }
    }
}

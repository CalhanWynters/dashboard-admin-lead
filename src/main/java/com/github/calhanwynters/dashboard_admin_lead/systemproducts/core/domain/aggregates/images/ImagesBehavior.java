package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

/**
 * Pure Behavioral Logic for Images.
 * Specific validation logic that extends the base SOC 2 protections.
 */
public final class ImagesBehavior {

    private ImagesBehavior() {}

    public record MetadataPatch(ImageName name, ImageDescription description) {}

    /**
     * Image-specific creation validation.
     */
    public static void validateCreation(ImageUuId uuId, ImagesBusinessUuId bUuId, Actor actor) {
        BaseAggregateRoot.verifyLifecycleAuthority(actor);
        DomainGuard.notNull(uuId, "Image UUID");
        DomainGuard.notNull(bUuId, "Business UUID");
    }

    /**
     * Image-specific rename logic.
     */
    public static ImageName evaluateRename(ImageName current, ImageName next, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify image names.", "SEC-403", actor);
        }
        DomainGuard.notNull(next, "New Image Name");
        if (next.equals(current)) {
            throw new IllegalArgumentException("New name must be different from current name.");
        }
        return next;
    }

    /**
     * Image-specific description logic.
     */
    public static ImageDescription evaluateDescriptionUpdate(ImageDescription current, ImageDescription next, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can modify image descriptions.", "SEC-403", actor);
        }
        DomainGuard.notNull(next, "New Image Description");
        if (next.equals(current)) {
            throw new IllegalArgumentException("New description must be different from current description.");
        }
        return next;
    }

    /**
     * Image-specific URL logic.
     */
    public static ImageUrl evaluateUrlUpdate(ImageUrl current, ImageUrl next, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Only Managers can update image URLs.", "SEC-403", actor);
        }
        DomainGuard.notNull(next, "New Image URL");
        if (next.equals(current)) {
            throw new IllegalArgumentException("New URL must be different from current URL.");
        }
        return next;
    }

    /**
     * SOC 2: System-level check for image reference recording.
     */
    public static void verifyReferenceAuthority(Actor actor) {
        if (Actor.SYSTEM.equals(actor)) return;
        if (!actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException("Insufficient privileges to record image references.", "SEC-403", actor);
        }
    }
}

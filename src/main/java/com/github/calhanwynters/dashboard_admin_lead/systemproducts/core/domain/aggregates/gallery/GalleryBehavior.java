package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.*;

public final class GalleryBehavior {

    private static final int MAX_GALLERY_SIZE = 50;

    private GalleryBehavior() {}

    public static void validateCreation(GalleryUuId uuId, GalleryBusinessUuId bUuId, Actor actor) {
        BaseAggregateRoot.verifyLifecycleAuthority(actor);
        DomainGuard.notNull(uuId, "Gallery UUID");
        DomainGuard.notNull(bUuId, "Business UUID");
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
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.ImageUuId;

/**
 * Pure Behavioral Logic for Gallery management.
 * Optimized for the "Two-Liner" pattern in the Aggregate.
 */
public final class GalleryBehavior {

    // Business rule: Hard limit for gallery capacity
    private static final int MAX_GALLERY_SIZE = 50;

    /**
     * Validates image addition and enforces capacity limits.
     * @throws IllegalStateException if the gallery is full.
     */
    public static ImageUuId evaluateImageAddition(ImageUuId imageUuId, int currentSize) {
        DomainGuard.notNull(imageUuId, "Image UUID");

        if (currentSize >= MAX_GALLERY_SIZE) {
            throw new IllegalStateException("Gallery limit reached. Cannot add more than %d images."
                    .formatted(MAX_GALLERY_SIZE));
        }

        return imageUuId;
    }

    /**
     * Validates image removal.
     */
    public static ImageUuId evaluateImageRemoval(ImageUuId imageUuId, boolean containsImage) {
        DomainGuard.notNull(imageUuId, "Image UUID");
        if (!containsImage) {
            throw new IllegalArgumentException("Image not found in this gallery.");
        }
        return imageUuId;
    }

    /**
     * Validates a change in public visibility.
     */
    public static boolean evaluatePublicityChange(boolean currentStatus, boolean newStatus) {
        if (currentStatus == newStatus) {
            throw new IllegalArgumentException("Gallery is already " + (currentStatus ? "public" : "private"));
        }
        return newStatus;
    }

    /**
     * Logic for reordering.
     */
    public static void verifyReorderable(int currentSize) {
        if (currentSize <= 1) {
            throw new IllegalStateException("Cannot reorder a gallery with fewer than 2 images.");
        }
    }

    /**
     * Safety check for deletions.
     */
    public static void verifyDeletable() {
        // Business logic: e.g., cross-check if this gallery is a 'primary' gallery
    }

    public static void verifyRestorable() {
        // Business logic for restoration
    }
}

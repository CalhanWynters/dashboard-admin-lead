package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.ImageUrl;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import java.util.HashSet;
import java.util.Set;

public class GalleryColFactory {

    /**
     * Creates a brand new Gallery. PrimaryKey is 0 as it hasn't been persisted yet.
     */
    public static GalleryCollection createNew(UuId businessId, Set<ImageUrl> imageUrls) {
        validate(imageUrls);
        // Defensive copy ensures the aggregate owns its data
        return new GalleryCollection(0, UuId.generate(), businessId, new HashSet<>(imageUrls));
    }

    /**
     * Reconstitutes an existing Gallery from the database.
     */
    public static GalleryCollection reconstitute(int primaryKey, UuId galleryColId, UuId businessId, Set<ImageUrl> imageUrls) {
        validate(imageUrls);
        return new GalleryCollection(primaryKey, galleryColId, businessId, new HashSet<>(imageUrls));
    }

    private static void validate(Set<ImageUrl> imageUrls) {
        if (imageUrls == null || imageUrls.isEmpty()) {
            throw new IllegalArgumentException("Gallery Invariant Violated: Must have at least one image URL.");
        }
        // Example of a platform-wide 2026 scale limit
        if (imageUrls.size() > 100) {
            throw new IllegalArgumentException("Gallery exceeds maximum capacity of 100 images.");
        }
    }
}

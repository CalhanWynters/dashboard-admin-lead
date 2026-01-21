package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.ImageUrl;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import java.util.Collections;
import java.util.Set;

/**
 * Represents the immutable state of a Gallery Aggregate.
 */
public class GalleryCollection {
    private final int primaryKey; // Database-specific ID
    private final UuId galleryColId; // Domain-specific Identity
    private final UuId businessId;
    private final Set<ImageUrl> imageUrls;

    // Constructor is package-private: Creation is restricted to the Factory
    GalleryCollection(int primaryKey, UuId galleryColId, UuId businessId, Set<ImageUrl> imageUrls) {
        this.primaryKey = primaryKey;
        this.galleryColId = galleryColId;
        this.businessId = businessId;
        // Final guard against external mutation
        this.imageUrls = Collections.unmodifiableSet(imageUrls);
    }

    /**
     * Entry point for domain logic.
     * Usage: gallery.act().removeImageUrl(url);
     */
    public GalleryColBehavior act() {
        return new GalleryColBehavior(this);
    }

    public int getPrimaryKey() { return primaryKey; }
    public UuId getGalleryColId() { return galleryColId; }
    public UuId getBusinessId() { return businessId; }
    public Set<ImageUrl> getImageUrls() { return imageUrls; }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.ImageUrl;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A representation of a Gallery that holds a collection of image URLs.
 */
public class GalleryCollection {

    private final int primaryKey; // Local database primary key
    private final UuId galleryColId; // Unique identifier for the gallery
    private final UuId businessId; // Unique identifier for the associated business
    private final Set<ImageUrl> imageUrls; // Set to hold unique ImageUrl objects

    /**
     * @param businessId Unique identifier for the business associated with this gallery
     */
    public GalleryCollection(int primaryKey, UuId businessId, Set<ImageUrl> imageUrls) {
        this.primaryKey = primaryKey; // Assign the local integer primary key
        this.galleryColId = UuId.generate(); // Generate a new unique identifier for the gallery
        this.businessId = businessId; // Assign the business ID
        this.imageUrls = Collections.unmodifiableSet(imageUrls); // Ensure the set is immutable
        validateImageUrls();
    }

    // ======================== Validation ========================================================

    private void validateImageUrls() {
        if (imageUrls.isEmpty()) {
            throw new IllegalArgumentException("Gallery must have at least one image URL.");
        }

        // Additional validation logic can go here, if needed
    }

    // ======================== Behavioral Methods ================================================

    // Common Getters
    public UuId getGalleryColId() {
        return galleryColId;
    }
    public UuId getBusinessId() {
        return businessId;
    }
    public int getPrimaryKey() {
        return primaryKey;
    }

    // Get & Remove
    public Set<ImageUrl> getImageUrls() {
        return imageUrls; // Return the immutable set directly
    }

    public GalleryCollection removeImageUrl(ImageUrl imageUrl) {
        if (!imageUrls.contains(imageUrl)) {
            throw new IllegalArgumentException("Image URL not found in gallery.");
        }

        Set<ImageUrl> updatedImageUrls = new HashSet<>(imageUrls);
        updatedImageUrls.remove(imageUrl);

        return new GalleryCollection(primaryKey, businessId, updatedImageUrls);
    }

    // Class Specific
    public int size() {return imageUrls.size();}
    public boolean contains(ImageUrl imageUrl) {return imageUrls.contains(imageUrl);}
}

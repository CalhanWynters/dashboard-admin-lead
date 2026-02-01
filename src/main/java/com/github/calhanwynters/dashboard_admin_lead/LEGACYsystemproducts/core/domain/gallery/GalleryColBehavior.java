package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.gallery;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.ImageUrl;
import java.util.HashSet;
import java.util.Set;

/**
 * Handles the domain logic and state transitions for a GalleryCollection.
 */
public class GalleryColBehavior {

    private final GalleryCollection collection;

    public GalleryColBehavior(GalleryCollection collection) {
        this.collection = collection;
    }

    public GalleryCollection removeImageUrl(ImageUrl imageUrl) {
        if (!collection.getImageUrls().contains(imageUrl)) {
            throw new IllegalArgumentException("Target image not found in this gallery.");
        }

        Set<ImageUrl> updatedImageUrls = new HashSet<>(collection.getImageUrls());
        updatedImageUrls.remove(imageUrl);

        return GalleryColFactory.reconstitute(
                collection.getPrimaryKey(),
                collection.getGalleryColId(),
                collection.getBusinessId(),
                updatedImageUrls
        );
    }

    public GalleryCollection addImageUrl(ImageUrl imageUrl) {
        Set<ImageUrl> updatedImageUrls = new HashSet<>(collection.getImageUrls());
        updatedImageUrls.add(imageUrl);

        return GalleryColFactory.reconstitute(
                collection.getPrimaryKey(),
                collection.getGalleryColId(),
                collection.getBusinessId(),
                updatedImageUrls
        );
    }

    public int size() { return collection.getImageUrls().size(); }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.*;

public final class ImagesBehavior {

    public record MetadataPatch(ImageName name, ImageDescription description) {}

    public static MetadataPatch evaluateMetadataUpdate(ImageName name, ImageDescription description) {
        DomainGuard.notNull(name, "Image Name");
        DomainGuard.notNull(description, "Image Description");
        return new MetadataPatch(name, description);
    }

    public static void verifyDeletable() {
        // Placeholder for rules (e.g., check if image is protected/system asset)
    }

    public static ImageDescription evaluateAltTextChange(ImageDescription current, ImageDescription next) {
        DomainGuard.notNull(next, "New Image Description");
        if (next.equals(current)) {
            throw new IllegalArgumentException("New alt text is identical to the current one.");
        }
        return next;
    }

    public static void verifyArchivable(boolean alreadyArchived) {
        if (alreadyArchived) {
            throw new IllegalStateException("Image is already archived.");
        }
    }
}

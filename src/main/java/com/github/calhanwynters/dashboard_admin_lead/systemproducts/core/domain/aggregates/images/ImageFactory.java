package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.ImageUrl;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

public class ImageFactory {

    /**
     * Delegates to the Aggregate's static factory to ensure the
     * ImageUploadedEvent is properly registered.
     */
    public static ImageAggregate create(
            ImageBusinessUuId bizId,
            ImageName name,
            ImageDescription desc,
            ImageUrl url,
            Actor creator) {

        return ImageAggregate.create(
                ImageUuId.generate(),
                bizId,
                name,
                desc,
                url,
                creator
        );
    }

    /**
     * Used by the Repository/Infrastructure layer to rebuild an existing entity.
     * Note: Reconstitution does NOT fire domain events.
     */
    public static ImageAggregate reconstitute(
            ImageId id,
            ImageUuId uuId,
            ImageBusinessUuId bizId,
            ImageName name,
            ImageDescription desc,
            ImageUrl url,
            boolean isArchived, // Added state for reconstitution
            AuditMetadata auditMetadata) {

        return new ImageAggregate(
                id,
                uuId,
                bizId,
                name,
                desc,
                url,
                isArchived, // 7th Arg
                auditMetadata // 8th Arg
        );
    }
}

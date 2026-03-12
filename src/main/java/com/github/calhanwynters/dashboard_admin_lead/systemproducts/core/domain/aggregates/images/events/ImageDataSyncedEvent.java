package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

/**
 * Event published when an admin manually triggers a Kafka sync.
 * Refactored for 2026 Edition: Uses LifecycleState record.
 */
@DomainEvent(name = "Image Data Synced", namespace = "images")
public record ImageDataSyncedEvent(
        ImageUuId imageUuId,
        ImagesBusinessUuId imagesBusinessUuId,
        ImageName imageName,
        ImageDescription imageDescription,
        ImageUrl imageUrl,
        LifecycleState lifecycleState,
        Actor actor
) { }

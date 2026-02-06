package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.events;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent(name = "Image Metadata Updated", namespace = "images")
public record ImageMetadataUpdatedEvent(
        ImageUuId imageUuId,
        ImageName newName,
        Actor updatedBy
) { }

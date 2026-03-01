package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.events;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent(name = "Image URL Updated", namespace = "image")
public record ImageUrlUpdatedEvent(
        ImageUuId imageUuId,
        ImageUrl newUrl,
        Actor updatedBy
) {}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.events;


import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import org.jmolecules.event.annotation.DomainEvent;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

@DomainEvent(name = "Image Archived", namespace = "images")
public record ImageArchivedEvent(
        ImageUuId imageUuId,
        Actor actor
) {}
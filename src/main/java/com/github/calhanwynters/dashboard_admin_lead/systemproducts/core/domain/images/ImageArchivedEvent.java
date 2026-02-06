package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images;


import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import org.jmolecules.event.annotation.DomainEvent;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.*;

@DomainEvent(name = "Image Archived", namespace = "images")
public record ImageArchivedEvent(
        ImageUuId imageUuId,
        Actor actor
) {}
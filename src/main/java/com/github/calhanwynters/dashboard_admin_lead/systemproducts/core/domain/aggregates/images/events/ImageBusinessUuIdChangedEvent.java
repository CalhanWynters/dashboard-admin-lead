package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.events;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent(name = "Images Business ID Changed", namespace = "image")
public record ImageBusinessUuIdChangedEvent(
        ImageUuId imageUuId,
        ImagesBusinessUuId oldId,
        ImagesBusinessUuId newId,
        Actor actor
) {}

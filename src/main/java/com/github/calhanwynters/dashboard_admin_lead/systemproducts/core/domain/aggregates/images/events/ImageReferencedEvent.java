package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.events;


import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import org.jmolecules.event.annotation.DomainEvent;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

/*
 * ImageReferencedEvent: In more complex systems, you might want to track when an image is
 * first linked to a Gallery or a Product. While usually handled by the GalleryAggregate,
 * a reciprocal event can be useful for Reference Counting to prevent deleting images that
 * are currently in use.
 */

@DomainEvent(name = "Image Referenced", namespace = "images")
public record ImageReferencedEvent(
        ImageUuId imageUuId,
        String referencedByEntity, // e.g., "GALLERY"
        UuId referencedByUuId,
        Actor actor
) {}
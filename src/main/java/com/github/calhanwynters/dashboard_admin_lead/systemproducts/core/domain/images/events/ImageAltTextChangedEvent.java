package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.events;


import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import org.jmolecules.event.annotation.DomainEvent;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.*;

/*
 * If ImageDescription is used for SEO or accessibility (alt tags),
 * business users often want to track this separately from a general
 * "metadata update" to ensure compliance.
 */
@DomainEvent(name = "Image Alt Text Changed", namespace = "images")
public record ImageAltTextChangedEvent(
        ImageUuId imageUuId,
        ImageDescription oldAltText,
        ImageDescription newAltText,
        Actor actor
) {}
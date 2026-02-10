package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

/**
 * Domain Event fired when a soft-deleted image is restored.
 */
@DomainEvent(name = "Image Restored", namespace = "images")
public record ImageRestoredEvent(ImageUuId imageUuId, Actor actor) {}

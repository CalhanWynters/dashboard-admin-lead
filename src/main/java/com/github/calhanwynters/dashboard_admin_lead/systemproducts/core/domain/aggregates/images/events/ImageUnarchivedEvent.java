package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

/**
 * Domain Event fired when an image is removed from the archive.
 */
@DomainEvent(name = "Image Unarchived", namespace = "images")
public record ImageUnarchivedEvent(ImageUuId imageUuId, Actor actor) {}

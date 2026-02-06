package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.ImageUrl;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.*;

@DomainEvent(name = "Image Uploaded", namespace = "images")
public record ImageUploadedEvent(ImageUuId imageUuId, ImageUrl imageUrl, Actor actor) {}

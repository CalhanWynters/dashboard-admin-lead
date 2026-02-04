package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.ImageUrl;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.images.ImagesDomainWrapper.*;

@DomainEvent(name = "Image Hard Deleted", namespace = "images")
public record ImageHardDeletedEvent(ImageUuId imageUuId, Actor actor) {}
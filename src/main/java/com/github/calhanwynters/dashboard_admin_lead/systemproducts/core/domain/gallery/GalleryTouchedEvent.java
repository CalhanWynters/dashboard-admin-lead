package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.*;

@DomainEvent(name = "Gallery Touched", namespace = "gallery")
public record GalleryTouchedEvent(
        GalleryUuId galleryUuId,
        Actor actor
) {}
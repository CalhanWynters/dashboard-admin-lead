package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

/**
 * Command representing the intent to create a new Gallery.
 * Handled by GalleryCreateHandler to initialize domain state.
 */
public record GalleryCreateCommand(
        GalleryUuId galleryUuId,
        GalleryBusinessUuId businessUuId,
        Actor actor
) {}

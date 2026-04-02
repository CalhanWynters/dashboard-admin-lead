package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

/**
 * Command representing the intent to add an Image to a Gallery.
 * Handled by GalleryAddImageHandler to enforce collection constraints.
 */
public record GalleryAddImageCommand(
        GalleryUuId galleryUuId,
        ImageUuId imageUuId,
        Actor actor
) {}

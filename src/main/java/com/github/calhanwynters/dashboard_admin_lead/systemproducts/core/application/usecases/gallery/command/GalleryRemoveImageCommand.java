package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

/**
 * Command representing the intent to remove an Image from a Gallery.
 * Handled by GalleryEditRemoveImageHandler.
 */
public record GalleryRemoveImageCommand(
        GalleryUuId galleryUuId,
        ImageUuId imageUuId,
        Actor actor
) {}

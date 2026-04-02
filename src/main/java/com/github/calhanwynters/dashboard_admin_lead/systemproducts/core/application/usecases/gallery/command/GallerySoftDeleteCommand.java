package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

/**
 * Command representing the intent to soft-delete a Gallery.
 * Handled by GallerySoftDeleteHandler to trigger SOC 2 compliant lifecycle transitions.
 */
public record GallerySoftDeleteCommand(
        GalleryUuId galleryUuId,
        Actor actor
) {}

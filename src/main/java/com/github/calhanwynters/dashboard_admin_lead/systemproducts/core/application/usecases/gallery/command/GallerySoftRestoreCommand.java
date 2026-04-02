package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

/**
 * Command representing the intent to restore a soft-deleted Gallery.
 * Handled by GallerySoftRestoreHandler to trigger SOC 2 restoration checks.
 */
public record GallerySoftRestoreCommand(
        GalleryUuId galleryUuId,
        Actor actor
) {}

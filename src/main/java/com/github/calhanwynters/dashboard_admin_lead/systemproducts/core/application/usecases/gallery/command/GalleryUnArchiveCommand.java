package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

/**
 * Command representing the intent to unarchive a Gallery.
 * Handled by GalleryUnArchiveHandler to restore active status.
 */
public record GalleryUnArchiveCommand(
        GalleryUuId galleryUuId,
        Actor actor
) {}

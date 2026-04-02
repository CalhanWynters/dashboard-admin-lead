package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

/**
 * Command representing the intent to archive a Gallery.
 * Handled by GalleryArchiveHandler to trigger lifecycle transitions.
 */
public record GalleryArchiveCommand(
        GalleryUuId galleryUuId,
        Actor actor
) {}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

/**
 * Command representing the intent to permanently remove a Gallery.
 * Handled with elevated security checks (Admin role required).
 */
public record GalleryHardDeleteCommand(
        GalleryUuId galleryUuId,
        Actor actor
) {}

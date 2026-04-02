package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.gallery.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;

/**
 * Command representing the intent to toggle a Gallery's public visibility.
 * Handled by GalleryTogglePublicStatusHandler for SOC 2 compliant state changes.
 */
public record GalleryTogglePublicStatusCommand(
        GalleryUuId galleryUuId,
        boolean newPublicStatus,
        Actor actor
) {}

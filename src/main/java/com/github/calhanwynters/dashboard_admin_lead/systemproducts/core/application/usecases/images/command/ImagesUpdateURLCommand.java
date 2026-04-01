package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to update an Image's Source URL.
 * Handled by ImagesUpdateURLHandler.
 */
public record ImagesUpdateURLCommand(
        UUID uuid,
        String newUrl,
        Actor actor
) {}

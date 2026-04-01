package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to unarchive an Image.
 * Triggers the transition back to an active state within the ImageAggregate.
 */
public record ImagesUnArchiveCommand(
        UUID uuid,
        Actor actor
) {}

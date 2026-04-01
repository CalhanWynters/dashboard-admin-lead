package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to archive an Image.
 * Handled by ImagesArchiveHandler to trigger SOC 2 lifecycle transitions.
 */
public record ImagesArchiveCommand(
        UUID uuid,
        Actor actor
) {}

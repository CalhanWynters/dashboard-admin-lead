package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to restore a soft-deleted Image.
 * Handled by ImageSoftRestoreHandler to trigger SOC 2 restoration checks.
 */
public record ImagesSoftRestoreCommand(
        UUID uuid,
        Actor actor
) {}

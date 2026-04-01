package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to archive a Feature.
 * Handled by FeaturesArchiveHandler to trigger SOC 2 lifecycle transitions.
 */
public record FeaturesArchiveCommand(
        UUID uuid,
        Actor actor
) {}

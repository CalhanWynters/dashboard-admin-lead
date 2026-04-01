package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to restore a soft-deleted Feature.
 * Handled by FeatureSoftRestoreHandler to trigger SOC 2 restoration checks.
 */
public record FeatureSoftRestoreCommand(
        UUID uuid,
        Actor actor
) {}

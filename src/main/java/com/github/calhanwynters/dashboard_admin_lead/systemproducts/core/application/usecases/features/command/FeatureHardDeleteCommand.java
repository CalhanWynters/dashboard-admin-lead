package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to permanently remove a Feature.
 * Handled with elevated security checks (Admin role required).
 */
public record FeatureHardDeleteCommand(
        UUID uuid,
        Actor actor
) {}

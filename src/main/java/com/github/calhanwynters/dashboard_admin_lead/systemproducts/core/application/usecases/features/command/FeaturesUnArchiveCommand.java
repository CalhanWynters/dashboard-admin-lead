package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to unarchive a Feature.
 * Triggers the transition back to an active state within the Aggregate.
 */
public record FeaturesUnArchiveCommand(
        UUID uuid,
        Actor actor
) {}

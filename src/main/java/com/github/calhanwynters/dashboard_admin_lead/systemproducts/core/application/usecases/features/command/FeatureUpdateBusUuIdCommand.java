package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to update a Feature's Business UUID.
 * Triggers the Admin-only verification logic in the BaseAggregateRoot.
 */
public record FeatureUpdateBusUuIdCommand(
        UUID uuid,
        String newBusinessUuid,
        Actor actor
) {}

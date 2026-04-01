package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to soft-delete a Feature.
 * Triggers ensureActive() and verifyLifecycleAuthority() in the aggregate.
 */
public record FeatureSoftDeleteCommand(
        UUID uuid,
        Actor actor
) {}

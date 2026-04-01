package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to soft-delete an Image.
 * Triggers ensureActive() and verifyLifecycleAuthority() in the aggregate.
 */
public record ImagesSoftDeleteCommand(
        UUID uuid,
        Actor actor
) {}

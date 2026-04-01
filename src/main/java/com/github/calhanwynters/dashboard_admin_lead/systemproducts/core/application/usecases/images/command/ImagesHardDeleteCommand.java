package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to permanently remove an Image.
 * Handled with elevated security checks (Admin role required).
 */
public record ImagesHardDeleteCommand(
        UUID uuid,
        Actor actor
) {}

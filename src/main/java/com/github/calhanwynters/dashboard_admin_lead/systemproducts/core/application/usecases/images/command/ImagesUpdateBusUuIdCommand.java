package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to update an Image's Business UUID.
 * Handled by ImagesUpdateBusUuIdHandler for SOC 2 compliant ID changes.
 */
public record ImagesUpdateBusUuIdCommand(
        UUID uuid,
        String newBusinessUuid,
        Actor actor
) {}

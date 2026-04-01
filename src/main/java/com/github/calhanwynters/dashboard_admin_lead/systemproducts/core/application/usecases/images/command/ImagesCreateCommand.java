package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to create/upload a new Image.
 * Flattens input for the ImagesCreateHandler.
 */
public record ImagesCreateCommand(
        UUID uuid,
        String businessUuid,
        String name,
        String description,
        String url,
        Actor actor
) {}

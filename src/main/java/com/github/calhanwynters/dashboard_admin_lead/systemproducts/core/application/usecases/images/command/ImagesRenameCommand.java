package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.images.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to rename an existing Image.
 * Uses standard Java types to decouple the API from Domain internals.
 */
public record ImagesRenameCommand(
        UUID uuid,
        String newName,
        Actor actor
) {}

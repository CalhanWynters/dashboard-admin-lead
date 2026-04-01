package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to rename an existing Feature.
 * Uses standard Java types to decouple the API from Domain internals.
 */
public record FeatureRenameCommand(
        UUID uuid,
        String newName,
        Actor actor
) {}

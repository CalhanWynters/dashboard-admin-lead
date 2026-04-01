package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to update a Feature's Compatibility Tag.
 * Handled by FeatureUpdateCompTagHandler.
 */
public record FeatureUpdateCompTagCommand(
        UUID uuid,
        String newTag,
        Actor actor
) {}

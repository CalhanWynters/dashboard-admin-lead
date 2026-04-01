package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.features.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.UUID;

/**
 * Command representing the intent to create a new Feature.
 * Uses primitive/standard types to stay decoupled from the Domain layer.
 */
public record FeatureCreateCommand(
        UUID uuid,
        String businessUuid,
        String name,
        String compatibilityTag,
        Actor actor
) {

}

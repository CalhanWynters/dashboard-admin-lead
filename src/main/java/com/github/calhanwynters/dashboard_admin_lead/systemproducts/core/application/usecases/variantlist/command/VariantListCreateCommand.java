package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.command;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import java.util.Set;

/**
 * Command for creating a new VariantList.
 * Acts as an immutable carrier for the initial business state and Actor context.
 */
public record VariantListCreateCommand(
        String businessUuid,
        Set<String> variantUuids,
        Actor actor
) {}
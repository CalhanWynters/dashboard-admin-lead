package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variantlist.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;

import java.util.Set;

/**
 * DTO for adding or removing a Variant from a VariantList.
 * Maps raw UUID input to hardened VariantsUuId records and Actor context.
 */
public record VariantListEditSetVariantDTO(
        String variantUuid,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened VariantsUuId.
     * Triggers DomainGuard validation for UUID syntax and length.
     */
    public VariantsUuId toVariantsUuId() {
        return new VariantsUuId(UuId.fromString(variantUuid));
    }

    /**
     * Maps to the common Actor record for domain logic authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

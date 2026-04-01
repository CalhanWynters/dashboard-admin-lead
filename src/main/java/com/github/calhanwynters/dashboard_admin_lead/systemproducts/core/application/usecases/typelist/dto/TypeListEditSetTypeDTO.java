package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;

import java.util.Set;

/**
 * DTO for adding or removing a Type from a TypeList.
 * Maps raw UUID input to hardened TypesUuId records and Actor context.
 */
public record TypeListEditSetTypeDTO(
        String typeUuid,
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the raw string into a hardened TypesUuId.
     * Triggers DomainGuard validation for UUID syntax and length.
     */
    public TypesUuId toTypesUuId() {
        return new TypesUuId(UuId.fromString(typeUuid));
    }

    /**
     * Maps to the common Actor record for domain logic authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

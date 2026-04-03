package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.dto.TypeListSoftDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;

/**
 * Orchestrates the conversion of TypeList soft-deletion requests.
 * Bridges the TypeList identity (Path) with the actor authorization payload (Body).
 */
public final class TypeListSoftMapper {

    private TypeListSoftMapper() { } // Static utility only

    /**
     * Maps the raw TypeList path ID and DTO payload into a validated Command.
     * Triggers DomainGuard if the uuid format is invalid.
     */
    public static TypeListSoftCommand toCommand(String uuid, TypeListSoftDTO dto) {
        DomainGuard.notBlank(uuid, "TypeList UUID Path Variable");

        return new TypeListSoftCommand(
                new TypeListUuId(UuId.fromString(uuid)),
                dto.toActor()
        );
    }

    /**
     * Internal Command record for the Use Case.
     * Pairs the target TypeList identity with the actor requesting the deletion.
     */
    public record TypeListSoftCommand(
            TypeListUuId typeListUuId,
            Actor actor
    ) {}
}

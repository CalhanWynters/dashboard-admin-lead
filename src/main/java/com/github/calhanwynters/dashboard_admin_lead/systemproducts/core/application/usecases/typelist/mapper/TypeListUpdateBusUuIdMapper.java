package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.typelist.dto.TypeListUpdateBusUuIdDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;

/**
 * Orchestrates the conversion of TypeList Business Identity update requests.
 * Bridges the TypeList identity (Path) with the new Business ID/Actor payload (Body).
 */
public final class TypeListUpdateBusUuIdMapper {

    private TypeListUpdateBusUuIdMapper() { } // Static utility only

    /**
     * Maps the raw TypeList path ID and DTO payload into a validated Command.
     * Triggers DomainGuard if the uuid or newBusinessUuid format is invalid.
     */
    public static TypeListUpdateBusUuIdCommand toCommand(String uuid, TypeListUpdateBusUuIdDTO dto) {
        DomainGuard.notBlank(uuid, "TypeList UUID Path Variable");

        return new TypeListUpdateBusUuIdCommand(
                new TypeListUuId(UuId.fromString(uuid)),
                dto.toTypeListBusinessUuId(),
                dto.toActor()
        );
    }

    /**
     * Internal Command record for the Use Case.
     * Groups the target TypeList identity, the new Business identity, and the Actor context.
     */
    public record TypeListUpdateBusUuIdCommand(
            TypeListUuId typeListUuId,
            TypeListBusinessUuId newBusinessUuid,
            Actor actor
    ) {}
}

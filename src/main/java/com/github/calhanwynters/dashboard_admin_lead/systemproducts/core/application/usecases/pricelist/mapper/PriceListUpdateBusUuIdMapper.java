package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto.PriceListUpdateBusUuIdDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListBusinessUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Orchestrates the conversion of PriceList Business Identity update requests.
 * Bridges the PriceList identity (Path) with the new Business ID/Actor payload (Body).
 */
public final class PriceListUpdateBusUuIdMapper {

    private PriceListUpdateBusUuIdMapper() { } // Static utility only

    /**
     * Maps the raw PriceList path ID and DTO payload into a validated Command.
     * Triggers DomainGuard if the uuid or newBusinessUuid format is invalid.
     */
    public static PriceListUpdateBusUuIdCommand toCommand(String uuid, PriceListUpdateBusUuIdDTO dto) {
        DomainGuard.notBlank(uuid, "PriceList UUID Path Variable");

        return new PriceListUpdateBusUuIdCommand(
                new PriceListUuId(UuId.fromString(uuid)),
                dto.toPriceListBusinessUuId(),
                dto.toActor()
        );
    }

    /**
     * Internal Command record for the Use Case.
     * Groups the target PriceList identity, the new Business identity, and the Actor context.
     */
    public record PriceListUpdateBusUuIdCommand(
            PriceListUuId priceListUuId,
            PriceListBusinessUuId newBusinessUuid,
            Actor actor
    ) {}
}

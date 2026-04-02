package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto.PriceListSoftDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Orchestrates the conversion of PriceList soft-deletion requests.
 * Bridges the PriceList identity (Path) with the actor authorization payload (Body).
 */
public final class PriceListSoftMapper {

    private PriceListSoftMapper() { } // Static utility only

    /**
     * Maps the raw PriceList path ID and DTO payload into a validated Command.
     * Triggers DomainGuard if the uuid format is invalid.
     */
    public static PriceListSoftCommand toCommand(String uuid, PriceListSoftDTO dto) {
        DomainGuard.notBlank(uuid, "PriceList UUID Path Variable");

        return new PriceListSoftCommand(
                new PriceListUuId(UuId.fromString(uuid)),
                dto.toActor()
        );
    }

    /**
     * Internal Command record for the Use Case.
     * Pairs the target PriceList identity with the actor requesting the deletion.
     */
    public record PriceListSoftCommand(
            PriceListUuId priceListUuId,
            Actor actor
    ) {}
}

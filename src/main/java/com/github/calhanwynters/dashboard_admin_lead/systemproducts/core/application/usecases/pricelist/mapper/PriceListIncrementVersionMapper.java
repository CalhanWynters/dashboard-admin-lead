package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto.PriceListIncrementVersionDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Orchestrates the conversion of PriceList version increment requests.
 * Bridges the PriceList identity (Path) with the authorization context (Body).
 */
public final class PriceListIncrementVersionMapper {

    private PriceListIncrementVersionMapper() { }

    /**
     * Maps the raw PriceList path ID and DTO payload into a validated Command.
     * Triggers DomainGuard if the uuid format is invalid.
     */
    public static PriceListIncrementVersionCommand toCommand(String uuid, PriceListIncrementVersionDTO dto) {
        DomainGuard.notBlank(uuid, "PriceList UUID Path Variable");

        return new PriceListIncrementVersionCommand(
                new PriceListUuId(UuId.fromString(uuid)),
                dto.toActor()
        );
    }

    /**
     * Internal Command record for the Use Case.
     * Pairs the target PriceList identity with the actor requesting the version bump.
     */
    public record PriceListIncrementVersionCommand(
            PriceListUuId priceListUuId,
            Actor actor
    ) {}
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto.PriceListActivationSwitchDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Orchestrates the conversion of PriceList activation toggle requests.
 */
public final class PriceListActivationSwitchMapper {

    private PriceListActivationSwitchMapper() { }

    public static PriceListActivationSwitchCommand toCommand(String uuid, PriceListActivationSwitchDTO dto) {
        DomainGuard.notBlank(uuid, "PriceList UUID Path Variable");

        return new PriceListActivationSwitchCommand(
                new PriceListUuId(UuId.fromString(uuid)),
                dto.active(),
                dto.toActor()
        );
    }

    public record PriceListActivationSwitchCommand(
            PriceListUuId priceListUuId,
            boolean active,
            Actor actor
    ) {}
}

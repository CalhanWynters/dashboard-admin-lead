package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto.PriceListArchiveDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Orchestrates the conversion of PriceList archival requests.
 */
public final class PriceListArchiveMapper {

    private PriceListArchiveMapper() { }

    public static PriceListArchiveCommand toCommand(String uuid, PriceListArchiveDTO dto) {
        DomainGuard.notBlank(uuid, "PriceList UUID Path Variable");

        return new PriceListArchiveCommand(
                new PriceListUuId(UuId.fromString(uuid)),
                dto.toActor()
        );
    }

    public record PriceListArchiveCommand(
            PriceListUuId priceListUuId,
            Actor actor
    ) {}
}

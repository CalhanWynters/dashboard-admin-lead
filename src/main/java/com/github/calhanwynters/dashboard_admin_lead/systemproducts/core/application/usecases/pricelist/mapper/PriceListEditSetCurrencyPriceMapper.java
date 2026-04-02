package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.mapper;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto.PriceListEditSetCurrencyPriceDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PurchasePricing;

import java.util.Currency;

/**
 * Orchestrates the conversion of specific currency price updates.
 * Bridges the PriceList identity (Path) with the Price Matrix update (Body).
 */
public final class PriceListEditSetCurrencyPriceMapper {

    private PriceListEditSetCurrencyPriceMapper() { }

    /**
     * Maps the raw PriceList path ID and DTO payload into a validated Command.
     * Triggers DomainGuard if the uuid format is invalid.
     */
    public static PriceListEditSetCurrencyPriceCommand toCommand(String uuid, PriceListEditSetCurrencyPriceDTO dto) {
        DomainGuard.notBlank(uuid, "PriceList UUID Path Variable");

        return new PriceListEditSetCurrencyPriceCommand(
                new PriceListUuId(UuId.fromString(uuid)),
                dto.toTargetUuId(),
                dto.toCurrency(),
                dto.pricing(),
                dto.toActor()
        );
    }

    /**
     * Internal Command record for the Use Case.
     * Pairs the PriceList identity with the specific Matrix coordinate (Target/Currency).
     */
    public record PriceListEditSetCurrencyPriceCommand(
            PriceListUuId priceListUuId,
            UuId targetUuId,
            Currency currency,
            PurchasePricing pricing,
            Actor actor
    ) {}
}

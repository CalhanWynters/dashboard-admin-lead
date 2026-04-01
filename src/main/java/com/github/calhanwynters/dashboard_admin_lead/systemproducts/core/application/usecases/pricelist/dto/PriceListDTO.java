package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PurchasePricing;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Currency;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public record PriceListDTO(
        UUID uuid,
        String businessUuid,
        String strategy,
        int version,           // Fix 1: Changed from String to int to match Version record
        boolean isActive,
        Map<UUID, Map<String, PurchasePricingDTO>> prices,
        boolean isArchived,
        boolean isSoftDeleted,
        Long version_lock,
        OffsetDateTime lastSyncedAt
) {
    public static PriceListDTO fromAggregate(PriceListAggregate aggregate) {
        return new PriceListDTO(
                aggregate.getUuId().value().asUUID(),
                aggregate.getBusinessUuId().value().value(),
                aggregate.getStrategyBoundary().name(),
                aggregate.getPriceListVersion().value().value(), // Extracts int from Version
                aggregate.isActive(),
                mapPrices(aggregate.getMultiCurrencyPrices()),
                aggregate.getLifecycleState().archived(),
                aggregate.getLifecycleState().softDeleted(),
                aggregate.getOptLockVer(),
                aggregate.getLastSyncedAt()
        );
    }

    private static Map<UUID, Map<String, PurchasePricingDTO>> mapPrices(
            Map<UuId, Map<Currency, PurchasePricing>> domainMap) {
        return domainMap.entrySet().stream().collect(Collectors.toMap(
                entry -> entry.getKey().asUUID(),
                entry -> entry.getValue().entrySet().stream().collect(Collectors.toMap(
                        currencyEntry -> currencyEntry.getKey().getCurrencyCode(),
                        currencyEntry -> PurchasePricingDTO.fromDomain(currencyEntry.getValue())
                ))
        ));
    }

    /**
     * DTO for the Polymorphic Pricing Model.
     * Since models vary, we export the 'Unit Price' (Price at Qty 1) as the baseline.
     */
    public record PurchasePricingDTO(
            BigDecimal unitPrice,
            String pricingModelType
    ) {
        public static PurchasePricingDTO fromDomain(PurchasePricing domain) {
            // Fix 2: Use the calculate(1) method to get the baseline amount for the DTO
            BigDecimal baseline = (domain == null) ? BigDecimal.ZERO : domain.calculate(BigDecimal.ONE).amount();
            String type = (domain == null) ? "NONE" : domain.getClass().getSimpleName();

            return new PurchasePricingDTO(baseline, type);
        }
    }
}

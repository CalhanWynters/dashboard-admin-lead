package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.application.usecases.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.application.usecases.pricelist.PriceListProjectionDTO.*;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.pricelist.PriceListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.pricelist.purchasepricingmodel.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PriceListProjectionMapper {

    public static PriceListProjectionDTO toDTO(PriceListAggregate aggregate) {
        Map<String, Map<String, PriceDetailDTO>> priceMap = new HashMap<>();

        aggregate.getMultiCurrencyPrices().forEach((targetId, currencyMap) -> {
            Map<String, PriceDetailDTO> currencyDtoMap = new HashMap<>();
            currencyMap.forEach((currency, pricing) ->
                    currencyDtoMap.put(currency.getCurrencyCode(), mapPricing(pricing))
            );
            priceMap.put(targetId.value(), currencyDtoMap);
        });

        // FIXED: 8 arguments provided to match PriceListProjectionDTO record
        return new PriceListProjectionDTO(
                aggregate.getPriceListUuId().value(),
                aggregate.getBusinessId().value(),
                "Standard Price List", // Default featuresName for 2026 snapshot
                aggregate.getStrategyBoundary().getSimpleName(),
                aggregate.getVersion().value(),
                aggregate.getAudit().createdAt().value(),    // Arg 6: OffsetDateTime
                aggregate.getAudit().lastModified().value(), // Arg 7: OffsetDateTime
                priceMap                                     // Arg 8: Map
        );
    }

    private static PriceDetailDTO mapPricing(PurchasePricing pricing) {
        // Each call now provides exactly 7 arguments:
        // (type, baseAmount, rateAmount, currency, tiers, isDiscrete, metadata)
        return switch (pricing) {
            case PriceFixedPurchase p ->
                    new PriceDetailDTO("FIXED", p.fixedPrice().amount(), BigDecimal.ZERO, p.fixedPrice().currency().getCurrencyCode(), null, true, Map.of());

            case PriceFractScaledPurchase p ->
                    new PriceDetailDTO("SCALED_FRACT", p.basePrice().amount(), p.ratePerUnit().amount(), p.ratePerUnit().currency().getCurrencyCode(), null, false, Map.of());

            case PriceIntScaledPurchase p ->
                    new PriceDetailDTO("SCALED_INT", p.basePrice().amount(), p.scalingFactorPerUnit().amount(), p.scalingFactorPerUnit().currency().getCurrencyCode(), null, true, Map.of());

            case PriceFractTieredGradPurchase p ->
                    new PriceDetailDTO("TIERED_GRAD_FRACT", null, null, p.buckets().getFirst().pricePerUnit().currency().getCurrencyCode(), mapTiers(p.buckets()), false, Map.of());

            case PriceIntTieredGradPurchase p ->
                    new PriceDetailDTO("TIERED_GRAD_INT", null, null, p.buckets().getFirst().pricePerUnit().currency().getCurrencyCode(), mapTiers(p.buckets()), true, Map.of());

            case PriceFractTieredVolPurchase p ->
                    new PriceDetailDTO("TIERED_VOL_FRACT", null, null, p.buckets().getFirst().pricePerUnit().currency().getCurrencyCode(), mapTiers(p.buckets()), false, Map.of());

            case PriceIntTieredVolPurchase p ->
                    new PriceDetailDTO("TIERED_VOL_INT", null, null, p.buckets().getFirst().pricePerUnit().currency().getCurrencyCode(), mapTiers(p.buckets()), true, Map.of());

            case PriceNonePurchase p ->
                    new PriceDetailDTO("NONE", BigDecimal.ZERO, BigDecimal.ZERO, p.currency().getCurrencyCode(), null, true, Map.of());
        };
    }

    private static List<TierDTO> mapTiers(List<?> buckets) {
        return buckets.stream()
                .map(obj -> switch (obj) {
                    case PriceFractTieredGradPurchase.TierBucket b -> new TierDTO(b.minQty(), b.maxQty(), b.pricePerUnit().amount());
                    case PriceIntTieredGradPurchase.TierBucket b -> new TierDTO(b.minQty(), b.maxQty(), b.pricePerUnit().amount());
                    case PriceFractTieredVolPurchase.TierBucket b -> new TierDTO(b.minQty(), b.maxQty(), b.pricePerUnit().amount());
                    case PriceIntTieredVolPurchase.TierBucket b -> new TierDTO(b.minQty(), b.maxQty(), b.pricePerUnit().amount());
                    default -> throw new IllegalArgumentException("Unknown tier type encountered in 2026 Snapshot");
                })
                .toList();
    }
}

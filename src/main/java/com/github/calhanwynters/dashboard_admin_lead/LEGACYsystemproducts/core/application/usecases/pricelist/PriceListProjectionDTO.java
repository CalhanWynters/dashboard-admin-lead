package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.application.usecases.pricelist;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Optimized Read-Model for UI/API consumption (2026).
 */
public record PriceListProjectionDTO(
        String priceListUuId,
        String businessId,
        String name,
        String strategyBoundary,
        int version,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        Map<String, Map<String, PriceDetailDTO>> prices
) {
    public record PriceDetailDTO(
            String type,
            BigDecimal baseAmount,
            BigDecimal rateAmount,
            String currency,
            List<TierDTO> tiers,
            boolean isDiscrete,
            Map<String, String> metadata
    ) {}



    public record TierDTO(
            BigDecimal minQty,
            BigDecimal maxQty,
            BigDecimal pricePerUnit
    ) {}
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist.dto;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PurchasePricing;

import java.util.Currency;
import java.util.Set;

/**
 * DTO for editing a specific currency price within a PriceList.
 * Maps raw inputs to Domain identifiers and pricing models.
 */
public record PriceListEditSetCurrencyPriceDTO(
        String targetUuid,       // The Product or Variant UUID
        String currencyCode,     // e.g., "USD", "EUR"
        PurchasePricing pricing,  // The strategy implementation (Fixed, Tiered, etc.)
        String actorId,
        Set<String> actorRoles
) {
    /**
     * Maps the target string to a domain UuId.
     */
    public UuId toTargetUuId() {
        return UuId.fromString(targetUuid);
    }

    /**
     * Maps the ISO code to a Java Currency object.
     */
    public Currency toCurrency() {
        return Currency.getInstance(currencyCode);
    }

    /**
     * Reconstructs the Actor for aggregate authorization.
     */
    public Actor toActor() {
        return Actor.of(actorId, actorRoles != null ? actorRoles : Set.of());
    }
}

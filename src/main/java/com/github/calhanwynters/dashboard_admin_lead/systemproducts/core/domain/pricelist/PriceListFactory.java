package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel.PurchasePricing;

import java.util.Currency;
import java.util.Map;

/**
 * Domain Factory for PriceList Aggregates (2026 Edition).
 * Manages initial creation and reconstitution from the data store.
 */
public class PriceListFactory {

    /**
     * Creates a brand new PriceList for a specific business and strategy boundary.
     */
    public static PriceListAggregate createNew(UuId businessId, Class<? extends PurchasePricing> boundary) {
        return new PriceListAggregate(
                PkId.of(0L),
                UuId.generate(),
                businessId,
                boundary,
                Version.INITIAL,
                AuditMetadata.create(),
                Map.of()
        );
    }

    /**
     * Reconstitutes an existing PriceList from persistence data.
     * Essential for mapping technical data structures back into valid domain objects.
     */
    public static PriceListAggregate reconstitute(
            PkId id,
            UuId uuId,
            UuId businessId,
            Class<? extends PurchasePricing> boundary,
            Version version,
            AuditMetadata audit,
            Map<UuId, Map<Currency, PurchasePricing>> prices) {

        return new PriceListAggregate(id, uuId, businessId, boundary, version, audit, prices);
    }
}

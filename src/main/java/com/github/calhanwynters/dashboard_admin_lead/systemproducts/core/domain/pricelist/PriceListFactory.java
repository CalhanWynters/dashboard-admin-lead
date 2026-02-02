package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.PriceListId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.PriceListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.PriceListBusinessUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.PriceListVersion;

import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel.PurchasePricing;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * Domain Factory for PriceList Aggregates (2026 Edition).
 * Manages initial creation and reconstitution from the data store.
 */
public class PriceListFactory {

    /**
     * Creates a brand new PriceList.
     * Uses delegated wrappers for 2026 Composition standards.
     */
    public static PriceListAggregate createNew(PriceListBusinessUuId businessId,
                                               Class<? extends PurchasePricing> boundary) {
        return new PriceListAggregate(
                PriceListId.of(0L),
                PriceListUuId.generate(),
                businessId,
                boundary,
                PriceListVersion.INITIAL,
                AuditMetadata.create(),
                new HashMap<>() // Use mutable map for initial creation
        );
    }

    /**
     * Reconstitutes an existing PriceList from persistence data.
     */
    public static PriceListAggregate reconstitute(
            PriceListId id,
            PriceListUuId uuId,
            PriceListBusinessUuId businessId,
            Class<? extends PurchasePricing> boundary,
            PriceListVersion version,
            AuditMetadata audit,
            Map<UuId, Map<Currency, PurchasePricing>> prices) {

        return new PriceListAggregate(id, uuId, businessId, boundary, version, audit, prices);
    }
}

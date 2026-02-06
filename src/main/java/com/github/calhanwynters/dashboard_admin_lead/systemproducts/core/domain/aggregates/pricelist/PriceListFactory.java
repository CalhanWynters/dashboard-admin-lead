package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PurchasePricing;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

import java.util.Currency;
import java.util.Map;

public class PriceListFactory {

    public static PriceListAggregate createNew(PriceListBusinessUuId businessId,
                                               Class<? extends PurchasePricing> boundary,
                                               Actor creator) {
        // Delegate to the aggregate's internal static factory
        return PriceListAggregate.create(PriceListUuId.generate(), businessId, boundary, creator);
    }

    public static PriceListAggregate reconstitute(
            PriceListId id,
            PriceListUuId uuId,
            Class<? extends PurchasePricing> boundary,
            PriceListVersion version,
            boolean isActive,
            AuditMetadata audit,
            Map<UuId, Map<Currency, PurchasePricing>> prices) {

        return new PriceListAggregate(id, uuId, boundary, version, isActive, audit, prices);
    }
}

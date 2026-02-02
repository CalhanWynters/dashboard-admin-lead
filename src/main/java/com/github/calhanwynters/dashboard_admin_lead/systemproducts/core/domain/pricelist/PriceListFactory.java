package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.purchasepricingmodel.PurchasePricing;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.*;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

public class PriceListFactory {

    public static PriceListAggregate createNew(PriceListBusinessUuId businessId,
                                               Class<? extends PurchasePricing> boundary,
                                               Actor creator) {
        return new PriceListAggregate(
                PriceListId.of(0L),
                PriceListUuId.generate(),
                businessId,
                boundary,
                PriceListVersion.INITIAL,
                AuditMetadata.create(creator), // Use the Actor
                new HashMap<>()
        );
    }

    public static PriceListAggregate reconstitute(
            PriceListId id, PriceListUuId uuId, PriceListBusinessUuId businessId,
            Class<? extends PurchasePricing> boundary, PriceListVersion version,
            AuditMetadata audit, Map<UuId, Map<Currency, PurchasePricing>> prices) {

        return new PriceListAggregate(id, uuId, businessId, boundary, version, audit, prices);
    }
}

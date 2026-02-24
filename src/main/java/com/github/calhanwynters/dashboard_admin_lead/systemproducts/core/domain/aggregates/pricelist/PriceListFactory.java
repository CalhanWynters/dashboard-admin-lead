package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PurchasePricing;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

import java.util.Currency;
import java.util.Map;

public class PriceListFactory {
    public static PriceListAggregate reconstitute(
            PriceListId id, PriceListUuId uuId, Class<? extends PurchasePricing> boundary, PriceListBusinessUuId businessUuId,
            PriceListVersion version, boolean isActive, ProductBooleans booleans,
            AuditMetadata audit, Map<UuId, Map<Currency, PurchasePricing>> prices) {

        return new PriceListAggregate(id, uuId, boundary, businessUuId, version, isActive, booleans, audit, prices);
    }
}

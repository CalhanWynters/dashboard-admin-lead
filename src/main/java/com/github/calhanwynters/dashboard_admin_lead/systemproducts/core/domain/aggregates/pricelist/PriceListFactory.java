package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleansLEGACY;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PricingStrategyType;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PurchasePricing;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

import java.util.Currency;
import java.util.Map;

public class PriceListFactory {
    public static PriceListAggregateLEGACY reconstitute(
            PriceListId id, PriceListUuId uuId, PricingStrategyType boundary, PriceListBusinessUuId businessUuId,
            PriceListVersion version, boolean isActive, ProductBooleansLEGACY booleans,
            AuditMetadata audit, Map<UuId, Map<Currency, PurchasePricing>> prices) {

        return new PriceListAggregateLEGACY(id, uuId, boundary, businessUuId, version, isActive, booleans, audit, prices);
    }
}

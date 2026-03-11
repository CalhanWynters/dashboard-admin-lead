package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListAggregateLEGACY;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PricingStrategyType;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

import java.util.Optional;
import java.util.List;

public interface PriceListRepository {

    // 1. IDENTITY & VERSION ACCESS
    Optional<PriceListAggregateLEGACY> findByUuId(PriceListUuId priceListUuId);

    Optional<PriceListAggregateLEGACY> findByBusinessUuId(PriceListBusinessUuId businessUuId);

    // 2. PERSISTENCE
    // Handles the complex Map<UuId, Map<Currency, PurchasePricing>> state
    void save(PriceListAggregateLEGACY aggregate);

    // 3. STRATEGY & STATE QUERIES
    // Useful for SOC 2 audits to see all lists under a specific boundary
    List<PriceListAggregateLEGACY> findByStrategy(PricingStrategyType strategy);

    // Returns the current "active" price list (isActive == true)
    List<PriceListAggregateLEGACY> findAllActive();

    // 4. VERSION CONTROL
    // Helpful if you need to compare the current version with a previous one
    Optional<PriceListAggregateLEGACY> findByBusinessUuIdAndVersion(
            PriceListBusinessUuId businessUuId,
            PriceListVersion version
    );

    // 5. LIFECYCLE
    void hardDelete(PriceListUuId priceListUuId);
}

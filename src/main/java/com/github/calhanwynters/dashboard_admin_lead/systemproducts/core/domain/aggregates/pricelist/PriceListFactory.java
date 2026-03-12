package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.Version;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PricingStrategyType;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PurchasePricing;

import java.time.OffsetDateTime;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

/**
 * Refactored PriceList Factory (2026 Edition).
 * Orchestrates complex multi-currency price list creation and reconstitution.
 */
public class PriceListFactory {

    private PriceListFactory() {}

    /**
     * Creation Factory
     * Initializes a fresh PriceList with a starting version and empty pricing map.
     */
    public static PriceListAggregate create(PriceListUuId uuId, PriceListBusinessUuId bUuId,
                                            PricingStrategyType strategy, Actor actor) {

        // SOC 2: Authority and ID validation
        PriceListBehavior.validateCreation(uuId, bUuId, actor);

        return new PriceListAggregate(
                null,
                uuId,
                bUuId,
                strategy,
                new PriceListVersion(new Version(1)),
                false,            // isActive (operational status)
                new HashMap<>(),  // initial prices map
                AuditMetadata.create(actor),
                new LifecycleState(false, false),
                0L,               // optLockVer
                1,                // schemaVer
                null              // lastSyncedAt
        );
    }

    /**
     * Reconstitution Factory
     * Restores complex nested state from persistence for financial auditing.
     */
    public static PriceListAggregate reconstitute(
            PriceListId id,
            PriceListUuId uuId,
            PriceListBusinessUuId businessUuId,
            PricingStrategyType boundary,
            PriceListVersion version,
            boolean isActive,
            AuditMetadata audit,
            LifecycleState lifecycleState,
            Map<UuId, Map<Currency, PurchasePricing>> prices,
            Long optLockVer,
            Integer schemaVer,
            OffsetDateTime lastSyncedAt) {

        return new PriceListAggregate(
                id, uuId, businessUuId, boundary, version, isActive, prices,
                audit, lifecycleState, optLockVer, schemaVer, lastSyncedAt
        );
    }
}

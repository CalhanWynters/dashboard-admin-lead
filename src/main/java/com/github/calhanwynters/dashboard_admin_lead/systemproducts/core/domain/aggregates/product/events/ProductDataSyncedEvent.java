package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState; // MIGRATED FROM LEGACY
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;

/**
 * SOC 2 Sync Event for Product Aggregate.
 */
@DomainEvent(name = "Product Data Synced", namespace = "products")
public record ProductDataSyncedEvent(
        ProductUuId productUuId,
        ProductBusinessUuId productBusinessUuId,
        ProductStatus productStatus,
        LifecycleState lifecycleState, // Standardized 2026 Type
        Actor actor
) { }

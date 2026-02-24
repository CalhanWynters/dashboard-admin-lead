package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;

@DomainEvent(name = "Product Data Synced", namespace = "product")
public record ProductDataSyncedEvent(
        ProductUuId productUuId,
        ProductBusinessUuId productBusinessUuId,
        ProductStatus productStatus,
        ProductBooleans productBooleans,
        Actor actor
) { }

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;

/*
 * If a background process discovers that the PriceListUuId assigned to this product no
 * longer exists (hard deleted), the product aggregate should fire an event to move itself
 * into a DRAFT or ERROR status.
 */
@DomainEvent(name = "Product Dependency Missing", namespace = "product")
public record ProductDependencyMissingEvent(
        ProductUuId productId,
        String dependencyType, // e.g., "PriceList"
        UuId missingUuId,
        Actor actor
) {}
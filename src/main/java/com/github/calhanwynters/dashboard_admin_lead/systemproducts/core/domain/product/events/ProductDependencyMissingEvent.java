package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.events;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId; // Ensure this is the correct import
import org.jmolecules.event.annotation.DomainEvent;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.ProductUuId;

@DomainEvent(name = "Product Dependency Missing", namespace = "product")
public record ProductDependencyMissingEvent(
        ProductUuId productId,
        String dependencyType,
        com.github.calhanwynters.dashboard_admin_lead.common.UuId missingUuId, // Use the common VO here
        Actor actor
) {}


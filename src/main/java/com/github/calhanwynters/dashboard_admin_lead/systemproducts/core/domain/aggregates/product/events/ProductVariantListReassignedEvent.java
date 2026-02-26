package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;

/**
 * Event fired when a product's structural link to a variant list is modified.
 */
@DomainEvent(name = "Product Variant List Reassigned", namespace = "product")
public record ProductVariantListReassignedEvent(
        ProductUuId productId,
        VariantListUuId newVariantListId,
        Actor actor
) {}
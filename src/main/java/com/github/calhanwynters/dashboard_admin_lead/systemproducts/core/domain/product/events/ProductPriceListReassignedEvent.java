package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.events;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.*;
import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;

@DomainEvent(name = "Product Price List Reassigned", namespace = "product")
public record ProductPriceListReassignedEvent(ProductUuId productId, PriceListUuId newPriceListId, Actor actor) {}

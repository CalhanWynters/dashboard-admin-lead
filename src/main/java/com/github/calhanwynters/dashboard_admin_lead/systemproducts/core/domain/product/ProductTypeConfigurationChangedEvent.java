package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.*;
import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;


@DomainEvent(name = "Product Type Configuration Changed", namespace = "product")
public record ProductTypeConfigurationChangedEvent(ProductUuId productId, TypeListUuId newTypeId, Actor actor) {}

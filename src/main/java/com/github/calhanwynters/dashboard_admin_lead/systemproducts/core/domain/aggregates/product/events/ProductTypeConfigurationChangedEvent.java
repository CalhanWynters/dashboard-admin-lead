package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.events;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper;
import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;


@DomainEvent(name = "Product Type Configuration Changed", namespace = "product")
public record ProductTypeConfigurationChangedEvent(ProductUuId productId, TypeListDomainWrapper.TypeListUuId newTypeId, Actor actor) {}

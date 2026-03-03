package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.events;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper;
import org.jmolecules.event.annotation.DomainEvent;

@DomainEvent(name = "Product Thumbnail URL Updated", namespace = "product")
public record ProductThumbnailUrlUpdatedEvent(
        ProductUuId productUuId,
        ProductThumbnailUrl newUrl,
        Actor updatedBy
) {}

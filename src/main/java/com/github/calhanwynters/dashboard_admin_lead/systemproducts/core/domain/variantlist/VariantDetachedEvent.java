package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.VariantsUuId;

@DomainEvent(name = "Variant Detached", namespace = "variantlist")
public record VariantDetachedEvent(
        VariantListUuId variantListUuId,
        VariantsUuId detachedVariantUuId,
        Actor actor
) {}
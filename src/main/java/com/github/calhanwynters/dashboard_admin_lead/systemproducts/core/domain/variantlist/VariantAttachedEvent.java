package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.VariantsUuId;

@DomainEvent(name = "Variant Attached", namespace = "variantlist")
public record VariantAttachedEvent(
        VariantListUuId variantListUuId,
        VariantsUuId attachedVariantUuId,
        Actor actor
) {}
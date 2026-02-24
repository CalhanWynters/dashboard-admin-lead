package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;
import java.util.Set;

@DomainEvent(name = "Variant List Data Synced", namespace = "variantlist")
public record VariantListDataSyncedEvent(
        VariantListUuId variantListUuId,
        VariantListBusinessUuId variantListBusinessUuId,
        Set<VariantsUuId> variantUuIds, // Matches VariantsDomainWrapper.VariantsUuId
        ProductBooleans productBooleans,
        Actor actor
) { }

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.events;

import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import java.util.Set;

@DomainEvent(name = "Variant Data Synced", namespace = "variants")
public record VariantDataSyncedEvent(
        VariantsUuId variantsUuId,
        VariantsBusinessUuId variantsBusinessUuId,
        VariantsName variantsName,
        Set<FeatureUuId> assignedFeatureUuIds, // Matches the new parameter
        ProductBooleans productBooleans,
        Actor actor
) { }

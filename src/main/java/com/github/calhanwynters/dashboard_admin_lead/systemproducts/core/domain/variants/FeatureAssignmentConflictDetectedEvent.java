package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.features.FeaturesDomainWrapper.*;
import org.jmolecules.event.annotation.DomainEvent;
import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variants.VariantsDomainWrapper.*;

@DomainEvent(name = "Feature Assignment Conflict Detected", namespace = "variants")
public record FeatureAssignmentConflictDetectedEvent(
        VariantsUuId variantId,
        FeatureUuId featureId,
        String details,
        Actor actor
) {}
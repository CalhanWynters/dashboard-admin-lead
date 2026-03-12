package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.FeatureUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.*;

import java.util.Optional;
import java.util.List;

public interface VariantsRepository {

    // 1. IDENTITY & BUSINESS LOOKUP
    Optional<VariantsAggregateLEGACY> findByUuId(VariantsUuId variantsUuId);

    Optional<VariantsAggregateLEGACY> findByBusinessUuId(VariantsBusinessUuId businessUuId);

    // 2. PERSISTENCE
    // Syncs the VariantsName, Set<FeatureUuId>, and ProductBooleans record
    void save(VariantsAggregateLEGACY aggregate);

    // 3. REVERSE LOOKUP
    // Identifies which variants are impacted if a Feature is modified or removed
    List<VariantsAggregateLEGACY> findAllByFeatureUuId(FeatureUuId featureUuId);

    // 4. COLLECTION QUERIES
    List<VariantsAggregateLEGACY> findAllActive();

    // 5. LIFECYCLE
    void hardDelete(VariantsUuId variantsUuId);
}

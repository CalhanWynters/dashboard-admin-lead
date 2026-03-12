package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeatureUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeatureBusinessUuId;

import java.util.Optional;
import java.util.List;

public interface FeaturesRepository {

    // Primary lookup by the Domain UUID
    Optional<FeaturesAggregateLEGACY> findByUuId(FeatureUuId uuId);

    // Business lookup (often needed for uniqueness checks)
    Optional<FeaturesAggregateLEGACY> findByBusinessUuId(FeatureBusinessUuId businessUuId);

    // Persistence
    void save(FeaturesAggregateLEGACY aggregate);

    // Collection-based queries (Filtered for active/non-deleted items usually)
    List<FeaturesAggregateLEGACY> findAllActive();

    // Specific deletion (if not handled purely by soft-delete in save)
    void hardDelete(FeatureUuId uuId);
}

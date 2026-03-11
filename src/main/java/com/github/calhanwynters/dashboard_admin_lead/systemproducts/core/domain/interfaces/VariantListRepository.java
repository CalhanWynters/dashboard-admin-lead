package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListAggregateLEGACY;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;

import java.util.Optional;
import java.util.List;

public interface VariantListRepository {

    // 1. IDENTITY & BUSINESS LOOKUP
    // Primary retrieval for domain actions
    Optional<VariantListAggregateLEGACY> findByUuId(VariantListUuId variantListUuId);

    // Essential for uniqueness checks during creation/business ID updates
    Optional<VariantListAggregateLEGACY> findByBusinessUuId(VariantListBusinessUuId businessUuId);

    // 2. PERSISTENCE
    // Manages the Set<VariantsUuId> and ProductBooleans record state
    void save(VariantListAggregateLEGACY aggregate);

    // 3. REVERSE LOOKUP
    // Find which lists contain a specific variant—crucial for SOC 2 dependency tracking
    List<VariantListAggregateLEGACY> findAllByContainsVariant(VariantsUuId variantUuId);

    // 4. COLLECTION QUERIES
    // Filters out soft-deleted and archived lists by default for standard operations
    List<VariantListAggregateLEGACY> findAllActive();

    // 5. LIFECYCLE
    void hardDelete(VariantListUuId variantListUuId);
}

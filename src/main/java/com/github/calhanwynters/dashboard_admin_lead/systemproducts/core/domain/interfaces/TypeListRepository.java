package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;

import java.util.Optional;
import java.util.List;

public interface TypeListRepository {

    // 1. IDENTITY & BUSINESS LOOKUP
    Optional<TypeListAggregateLEGACY> findByUuId(TypeListUuId typeListUuId);

    Optional<TypeListAggregateLEGACY> findByBusinessUuId(TypeListBusinessUuId businessUuId);

    // 2. PERSISTENCE
    // Responsible for syncing the Set<TypesUuId> and ProductBooleans state
    void save(TypeListAggregateLEGACY aggregate);

    // 3. REVERSE LOOKUP
    // Find which lists contain a specific type (useful for dependency checks before type deletion)
    List<TypeListAggregateLEGACY> findAllByContainsType(TypesUuId typeUuId);

    // 4. COLLECTION QUERIES
    List<TypeListAggregateLEGACY> findAllActive();

    // 5. LIFECYCLE
    void hardDelete(TypeListUuId typeListUuId);
}

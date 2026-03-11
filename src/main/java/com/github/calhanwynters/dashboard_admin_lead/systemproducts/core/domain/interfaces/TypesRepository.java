package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesAggregateLEGACY;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.*;

import java.util.Optional;
import java.util.List;

public interface TypesRepository {

    // 1. IDENTITY & BUSINESS LOOKUP
    Optional<TypesAggregateLEGACY> findByUuId(TypesUuId typesUuId);

    Optional<TypesAggregateLEGACY> findByBusinessUuId(TypesBusinessUuId businessUuId);

    // 2. PERSISTENCE
    // Handles the complex TypesPhysicalSpecs and ProductBooleans record
    void save(TypesAggregateLEGACY aggregate);

    // 3. SEARCH & VALIDATION
    // Useful for UI lookups and ensuring name uniqueness if required by business logic
    Optional<TypesAggregateLEGACY> findByName(TypesName typesName);

    // 4. COLLECTION QUERIES
    List<TypesAggregateLEGACY> findAllActive();

    // 5. LIFECYCLE
    void hardDelete(TypesUuId typesUuId);
}

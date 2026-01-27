package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.type;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.dto.TypeColProjectionDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.mappers.TypeColProjectionMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.TypeColQueryRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Application Service for Querying Type Collections.
 * Orchestrates the retrieval of Aggregate Snapshots and their projection to DTOs.
 */
public class TypeColQueryService {

    private final TypeColQueryRepository queryRepository;

    public TypeColQueryService(TypeColQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    /**
     * Retrieves a single TypeCollection projection by ID.
     */
    public Optional<TypeColProjectionDTO> getTypeCollectionById(UuId typeColId) {
        return queryRepository.findById(typeColId)
                .map(TypeColProjectionMapper::toDto);
    }

    /**
     * Retrieves all TypeCollection projections for a specific business.
     */
    public List<TypeColProjectionDTO> getAllCollectionsByBusiness(UuId businessId) {
        return queryRepository.findAllByBusinessId(businessId)
                .stream()
                .map(TypeColProjectionMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Business logic check: Does a collection exist for this business?
     */
    public boolean collectionExists(UuId businessId) {
        return queryRepository.existsByBusinessId(businessId);
    }
}

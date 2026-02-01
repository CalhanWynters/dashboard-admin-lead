package com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.application.usecases.variant;

import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.VariantColQueryRepository;
import com.github.calhanwynters.dashboard_admin_lead.LEGACYsystemproducts.core.domain.common.UuId;

import java.util.List;
import java.util.Optional;

public class VariantColQueryService {
    private final VariantColQueryRepository repository;

    public VariantColQueryService(VariantColQueryRepository repository) {
        this.repository = repository;
    }

    public Optional<VariantColProjectionDTO> getCollectionById(UuId id) {
        return repository.findById(id)
                .map(VariantColProjectionMapper::toDTO);
    }

    public List<VariantColProjectionDTO> getCollectionsByBusiness(UuId businessId) {
        return repository.findAllByBusinessId(businessId).stream()
                .map(VariantColProjectionMapper::toDTO)
                .toList();
    }
}

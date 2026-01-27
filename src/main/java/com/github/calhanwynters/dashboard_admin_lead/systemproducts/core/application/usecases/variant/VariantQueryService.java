package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.variant;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.dto.VariantColProjectionDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.mappers.VariantColProjectionMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.VariantColQueryRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variant.FeatureCompatibilityPolicy;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.validationchecks.DomainGuard;

import java.util.Optional;

public class VariantQueryService {

    private final VariantColQueryRepository repository;
    private final FeatureCompatibilityPolicy globalPolicy;

    public VariantQueryService(VariantColQueryRepository repository, FeatureCompatibilityPolicy globalPolicy) {
        this.repository = repository;
        this.globalPolicy = globalPolicy;
    }

    public Optional<VariantColProjectionDTO> getVariantById(String variantId) {
        // Enforce 2026 Domain Integrity: ensure the string is a valid UUID before querying
        DomainGuard.notBlank(variantId, "Variant ID");

        // FIX: Pass the String directly to UuId.fromString or the constructor
        UuId domainId = UuId.fromString(variantId);

        return repository.findById(domainId)
                .map(collection -> VariantColProjectionMapper.toDTO(collection, globalPolicy));
    }

    public boolean variantExists(String variantId) {
        DomainGuard.notBlank(variantId, "Variant ID");
        return repository.existsByBusinessId(UuId.fromString(variantId));
    }
}

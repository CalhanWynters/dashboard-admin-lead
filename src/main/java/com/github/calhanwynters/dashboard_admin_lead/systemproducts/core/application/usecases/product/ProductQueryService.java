package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.product;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.dto.ProductProjectionDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.mappers.ProductProjectionMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.ProductQueryRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.validationchecks.DomainGuard;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Orchestrates Read-Model projections for the Product Bounded Context (2026).
 * Handles input validation and provides snapshots for UI consumption.
 */
public class ProductQueryService {

    private final ProductQueryRepository repository;

    public ProductQueryService(ProductQueryRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves a full UI snapshot of a product.
     * Enforces UuId validation before hitting the infrastructure layer.
     */
    public Optional<ProductProjectionDTO> getProductSnapshot(String productUuid) {
        DomainGuard.notBlank(productUuid, "Product ID");
        UuId id = UuId.fromString(productUuid);

        return repository.findById(id)
                .map(ProductProjectionMapper::toDTO);
    }

    /**
     * Specialized method for 2026 Concurrency Checks.
     * Allows the UI or Command handlers to verify the snapshot age without loading the full blob.
     */
    public OffsetDateTime getLatestStateTimestamp(String productUuid) {
        DomainGuard.notBlank(productUuid, "Product ID");
        return repository.getLatestTimestamp(UuId.fromString(productUuid));
    }

    /**
     * Lists all products for a specific business dashboard.
     */
    public List<ProductProjectionDTO> getBusinessDashboard(String businessId) {
        DomainGuard.notBlank(businessId, "Business ID");

        return repository.findAllByBusinessId(UuId.fromString(businessId))
                .stream()
                .map(ProductProjectionMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Quick existence check to prevent command processing on non-existent targets.
     */
    public boolean exists(String productUuid) {
        if (productUuid == null || productUuid.isBlank()) return false;
        return repository.existsByUuId(UuId.fromString(productUuid));
    }
}

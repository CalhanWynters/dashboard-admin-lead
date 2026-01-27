package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.usecases.pricelist;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.dto.PriceListProjectionDTO;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.application.mappers.PriceListProjectionMapper;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.PriceListQueryRepository;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;

import java.util.Currency;
import java.util.List;
import java.util.Optional;

/**
 * Application Service for Read-Side operations.
 * Orchestrates the retrieval and mapping of PriceList snapshots.
 */
public class PriceListQueryService {

    private final PriceListQueryRepository repository;

    public PriceListQueryService(PriceListQueryRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves a specific PriceList snapshot by ID.
     */
    public Optional<PriceListProjectionDTO> getPriceListById(UuId priceListUuId) {
        return repository.findById(priceListUuId)
                .map(PriceListProjectionMapper::toDTO);
    }

    /**
     * Retrieves all price lists for a specific business context.
     */
    public List<PriceListProjectionDTO> getBusinessPriceLists(UuId businessId) {
        return repository.findAllByBusinessId(businessId).stream()
                .map(PriceListProjectionMapper::toDTO)
                .toList();
    }

    /**
     * 2026 Strategy-Specific Query: Returns lists matching a pricing strategy (e.g., Tiered).
     */
    public List<PriceListProjectionDTO> getByStrategyType(Class<?> strategyClass) {
        return repository.findByStrategyBoundary(strategyClass).stream()
                .map(PriceListProjectionMapper::toDTO)
                .toList();
    }

    /**
     * Localized Currency Query: Finds lists supporting a specific currency.
     */
    public List<PriceListProjectionDTO> getByCurrency(Currency currency) {
        return repository.findAllByCurrency(currency).stream()
                .map(PriceListProjectionMapper::toDTO)
                .toList();
    }
}

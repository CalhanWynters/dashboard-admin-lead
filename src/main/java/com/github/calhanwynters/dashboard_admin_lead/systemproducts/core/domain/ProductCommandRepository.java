package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common.UuId;

import java.util.Optional;

/**
 * Domain-driven Command Repository interface.
 * Focused strictly on state-changing operations and Aggregate integrity.
 */
public interface ProductCommandRepository {

    /**
     * Persists the current state of a Product.
     * Must handle both Creation and Updating (Snapshots).
     *
     * @param product The aggregate to save.
     * @return true if successful, false otherwise.
     * @throws RuntimeException if an optimistic locking version mismatch occurs.
     */
    boolean save(ProductAggregateRoot product);

    /**
     * Marks a product as deleted in the system.
     *
     * @param productUuId The unique domain identifier of the product.
     * @return true if the status transition was successful.
     */
    boolean softDelete(UuId productUuId);

    /**
     * Permanently removes a product record from the system.
     *
     * @param productUuId The unique domain identifier of the product.
     * @return true if the record was removed.
     */
    boolean hardDelete(UuId productUuId);

    /**
     * Utility check used by the Domain to verify existence
     * before critical operations.
     */
    boolean exists(UuId productUuId);
}

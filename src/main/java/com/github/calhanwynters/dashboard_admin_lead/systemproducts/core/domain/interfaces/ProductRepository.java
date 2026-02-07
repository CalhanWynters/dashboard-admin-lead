package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductAggregateRoot;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;

import java.util.Optional;
import java.util.List;

/**
 * Domain Repository for Product Aggregates.
 * Manages the persistence of Product state, including its manifest, status, and weak references.
 */
public interface ProductRepository {

    /**
     * Reconstitutes the Product via its internal Technical Identity.
     */
    Optional<ProductAggregateRoot> findByUuId(ProductUuId uuId);

    /**
     * Reconstitutes the Product via its External/Business Identity.
     */
    Optional<ProductAggregateRoot> findByBusinessUuId(ProductBusinessUuId businessUuId);

    /**
     * Retrieves all products matching a specific status (e.g., ACTIVE, DRAFT).
     */
    List<ProductAggregateRoot> findByStatus(ProductStatus status);

    /**
     * Persists the Product state.
     * Implementation must handle the conversion of Value Objects (Manifest, PhysicalSpecs)
     * and the AuditMetadata to the database schema.
     */
    void save(ProductAggregateRoot product);

    /**
     * Checks if a Business UUID is already taken before creation.
     */
    boolean existsByBusinessUuId(ProductBusinessUuId businessUuId);

    /**
     * Removes the product from the store.
     * (Usually reserved for hard-delete or cleanup operations).
     */
    void delete(ProductAggregateRoot product);
}

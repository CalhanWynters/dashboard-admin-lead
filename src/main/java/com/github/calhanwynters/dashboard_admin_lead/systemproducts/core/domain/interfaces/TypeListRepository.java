package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.interfaces;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListAggregate;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;

import java.util.Optional;
import java.util.List;

/**
 * Domain Repository for Type List Aggregates.
 * Manages collections of Type references and their associated lifecycle states.
 */
public interface TypeListRepository {

    /**
     * Reconstitutes the TypeList from persistence via its internal UUID.
     */
    Optional<TypeListAggregate> findByUuId(TypeListUuId uuId);

    /**
     * Reconstitutes the TypeList via its Business/External Identity.
     */
    Optional<TypeListAggregate> findByBusinessUuId(TypeListBusinessUuId businessUuId);

    /**
     * Persists the state of the TypeList, including its set of Type references.
     * Implementation should handle the 'deleted' flag mapping.
     */
    void save(TypeListAggregate aggregate);

    /**
     * Retrieves all active (non-deleted) Type Lists.
     */
    List<TypeListAggregate> findActive();

    /**
     * Hard-deletes the TypeList from the store.
     */
    void delete(TypeListAggregate aggregate);

    /**
     * Existence check for Business Identity uniqueness.
     */
    boolean existsByBusinessUuId(TypeListBusinessUuId businessUuId);
}

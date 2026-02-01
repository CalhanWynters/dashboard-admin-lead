package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.TypeListId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.TypeListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.TypeListBusinessUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.types.TypesDomainWrapper.TypesUuId;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TypeListAggregate extends AbstractAggregateRoot<TypeListAggregate> {

    private final TypeListId typeListId;
    private final TypeListUuId typeListUuId;
    private final TypeListBusinessUuId typeListBusinessUuId;
    private final Set<TypesUuId> typeUuIds;

    public TypeListAggregate(TypeListId typeListId,
                             TypeListUuId typeListUuId,
                             TypeListBusinessUuId typeListBusinessUuId,
                             Set<TypesUuId> typeUuIds) {
        // Validation checks
        DomainGuard.notNull(typeListId, "TypeListAggregate ID");
        DomainGuard.notNull(typeListUuId, "TypeListAggregate UUID");
        DomainGuard.notNull(typeListBusinessUuId, "TypeListAggregate Business UUID");
        DomainGuard.notNull(typeUuIds, "Type UUID Set");

        this.typeListId = typeListId;
        this.typeListUuId = typeListUuId;
        this.typeListBusinessUuId = typeListBusinessUuId;
        // Defensive copy to protect internal state
        this.typeUuIds = new HashSet<>(typeUuIds);
    }

    // Getters
    public TypeListId getTypeListId() {
        return typeListId;
    }

    public TypeListUuId getTypeListUuId() {
        return typeListUuId;
    }

    public TypeListBusinessUuId getTypeListBusinessUuId() {
        return typeListBusinessUuId;
    }

    public Set<TypesUuId> getTypeUuIds() {
        // Return unmodifiable to ensure DDD encapsulation
        return Collections.unmodifiableSet(typeUuIds);
    }

    /**
     * Business method to add a type to the list.
     */
    public void addType(TypesUuId typeUuId) {
        DomainGuard.notNull(typeUuId, "Type UUID");
        this.typeUuIds.add(typeUuId);
    }
}

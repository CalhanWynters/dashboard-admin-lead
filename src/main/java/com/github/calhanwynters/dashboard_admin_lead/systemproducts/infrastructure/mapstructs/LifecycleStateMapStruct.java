package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.LifecycleStateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LifecycleStateMapStruct {

    /**
     * Maps Domain Record -> JPA Entity (Freezing)
     * MapStruct automatically matches record methods (archived(), softDeleted())
     * to entity setters (setArchived(), setSoftDeleted()).
     */
    LifecycleStateEntity toEntity(LifecycleState record);

    /**
     * Maps JPA Entity -> Domain Record (Thawing)
     * MapStruct uses the record's canonical constructor to instantiate
     * the immutable domain object.
     */
    LifecycleState toRecord(LifecycleStateEntity entity);
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.FeaturesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

@Mapper(componentModel = "spring")
public interface FeaturesMapper {

    // TO AGGREGATE
    @Mapping(target = "id", source = "id", qualifiedByName = "toFeatureId")
    @Mapping(target = "uuId", source = "uuid", qualifiedByName = "toFeatureUuId")
    @Mapping(target = "businessUuId", source = "businessUuid", qualifiedByName = "toBusinessUuId")
    @Mapping(target = "featuresName", source = "name", qualifiedByName = "toFeatureName")
    @Mapping(target = "compatibilityTag", source = "label", qualifiedByName = "toFeatureLabel")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    @Mapping(target = "lifecycleState", source = ".", qualifiedByName = "toLifecycleState")
    @Mapping(target = "optLockVer", source = "version") // Assuming entity has a version field
    @Mapping(target = "schemaVer", constant = "1")
    FeaturesAggregate toAggregate(FeaturesEntity entity);

    // TO ENTITY (Unwrapping the domain objects)
    @Mapping(target = "id", source = "id.value.id")
    @Mapping(target = "uuid", source = "uuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "businessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "name", source = "featuresName.value.value")
    @Mapping(target = "label", source = "compatibilityTag.value.value")
    @Mapping(target = "archived", source = "lifecycleState.archived")
    @Mapping(target = "softDeleted", source = "lifecycleState.softDeleted")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    @Mapping(target = "version", source = "optLockVer")
    FeaturesEntity toEntity(FeaturesAggregate aggregate);

    // --- MAPPING HELPERS ---

    @Named("toFeatureId")
    default FeatureId toFeatureId(Long id) {
        return id != null ? new FeatureId(PkId.of(id)) : null;
    }

    @Named("toFeatureUuId")
    default FeatureUuId toFeatureUuId(java.util.UUID uuid) {
        return uuid != null ? new FeatureUuId(UuId.fromString(uuid.toString())) : null;
    }

    @Named("toBusinessUuId")
    default FeatureBusinessUuId toBusinessUuId(java.util.UUID uuid) {
        return uuid != null ? new FeatureBusinessUuId(UuId.fromString(uuid.toString())) : null;
    }

    @Named("toFeatureName")
    default FeatureName toFeatureName(String name) {
        return name != null ? new FeatureName(new Name(name)) : null;
    }

    @Named("toFeatureLabel")
    default FeatureLabel toFeatureLabel(String label) {
        return label != null ? new FeatureLabel(new Label(label)) : null;
    }

    @Named("toLifecycleState")
    default LifecycleState toLifecycleState(FeaturesEntity entity) {
        return new LifecycleState(entity.isArchived(), entity.isSoftDeleted());
    }

    @Named("toAuditMetadata")
    default AuditMetadata toAuditMetadata(FeaturesEntity entity) {
        return AuditMetadata.reconstitute(
                new CreatedAt(entity.getCreatedAt()),
                new LastModified(entity.getLastModifiedAt()),
                new Actor(entity.getLastModifiedBy(), Collections.emptySet())
        );
    }

    @Named("stringToUuid")
    default java.util.UUID stringToUuid(String value) {
        return value != null ? java.util.UUID.fromString(value) : null;
    }
}

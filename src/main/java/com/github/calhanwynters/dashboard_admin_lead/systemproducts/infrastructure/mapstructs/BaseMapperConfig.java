package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.AuditMetadataEntity;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.LifecycleStateEntity;
import org.mapstruct.Mapper;

import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface BaseMapperConfig {

    // --- IDENTITY MAPPING ---
    default Long map(PkId id) { return id == null ? null : id.value(); }
    default String map(UuId uuid) { return uuid == null ? null : uuid.value(); }

    default PkId toPkId(Long value) { return value == null ? null : new PkId(value); }
    default UuId toUuId(String value) { return value == null ? null : new UuId(value); }

    // --- LIFECYCLE STATE MAPPING ---
    default LifecycleStateEntity map(LifecycleState domain) {
        if (domain == null) return null;
        LifecycleStateEntity entity = new LifecycleStateEntity();
        entity.setArchived(domain.archived());
        entity.setSoftDeleted(domain.softDeleted());
        return entity;
    }

    default LifecycleState map(LifecycleStateEntity entity) {
        if (entity == null) return null;
        return new LifecycleState(entity.isArchived(), entity.isSoftDeleted());
    }

    // --- AUDIT METADATA MAPPING ---
    default AuditMetadataEntity map(AuditMetadata domain) {
        if (domain == null) return null;
        AuditMetadataEntity entity = new AuditMetadataEntity();

        // FIX: Convert OffsetDateTime to Instant for the Entity
        entity.setCreatedAt(domain.createdAt().value().toInstant());
        entity.setLastModified(domain.lastModified().value().toInstant());

        // FIX: Use .identity() instead of .value() for Actor
        entity.setLastModifiedBy(domain.lastModifiedBy().identity());
        return entity;
    }

    default AuditMetadata map(AuditMetadataEntity entity) {
        if (entity == null) return null;

        // FIX: Reconstitute OffsetDateTime from Instant using UTC
        return AuditMetadata.reconstitute(
                new CreatedAt(entity.getCreatedAt().atOffset(ZoneOffset.UTC)),
                new LastModified(entity.getLastModified().atOffset(ZoneOffset.UTC)),
                // Note: Since roles aren't stored in AuditMetadataEntity,
                // we reconstitute with an empty set or lookup based on identity.
                new Actor(entity.getLastModifiedBy(), java.util.Collections.emptySet())
        );
    }
}

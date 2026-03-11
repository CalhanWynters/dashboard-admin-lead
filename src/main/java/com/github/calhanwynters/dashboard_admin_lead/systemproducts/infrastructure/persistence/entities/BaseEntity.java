package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities;

import jakarta.persistence.*;

@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id; // MapStruct will convert this to PkId

    protected String uuId; // MapStruct will convert this to UuId
    protected String businessUuId;

    @Version
    protected Long optLockVer; // JPA handles the lock here

    protected Integer schemaVersion;

    // Entity-specific versions of your composite classes
    @Embedded protected AuditMetadataEntity auditMetadata;
    @Embedded protected LifecycleStateEntity lifecycleState;
}
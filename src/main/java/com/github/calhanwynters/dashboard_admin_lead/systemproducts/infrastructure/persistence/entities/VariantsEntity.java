package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "system_variants")
public class VariantsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "business_uuid", nullable = false)
    private UUID businessUuid;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "is_archived", nullable = false)
    private boolean archived;

    @Column(name = "is_soft_deleted", nullable = false)
    private boolean softDeleted;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "variant_assigned_features", joinColumns = @JoinColumn(name = "variant_id"))
    @Column(name = "feature_uuid")
    private Set<UUID> assignedFeatureUuids = new HashSet<>();

    // Audit Fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "last_modified_at", nullable = false)
    private OffsetDateTime lastModifiedAt;

    @Column(name = "last_modified_by", nullable = false)
    private String lastModifiedBy;

    public VariantsEntity() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public UUID getBusinessUuid() { return businessUuid; }
    public void setBusinessUuid(UUID businessUuid) { this.businessUuid = businessUuid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }

    public boolean isSoftDeleted() { return softDeleted; }
    public void setSoftDeleted(boolean softDeleted) { this.softDeleted = softDeleted; }

    public Set<UUID> getAssignedFeatureUuids() { return assignedFeatureUuids; }
    public void setAssignedFeatureUuids(Set<UUID> assignedFeatureUuids) { this.assignedFeatureUuids = assignedFeatureUuids; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getLastModifiedAt() { return lastModifiedAt; }
    public void setLastModifiedAt(OffsetDateTime lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "system_type_lists")
public class TypeListEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "business_uuid", nullable = false)
    private UUID businessUuid;

    @Column(name = "is_archived", nullable = false)
    private boolean archived;

    @Column(name = "is_soft_deleted", nullable = false)
    private boolean softDeleted;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "type_list_items", joinColumns = @JoinColumn(name = "type_list_id"))
    @Column(name = "type_uuid")
    private Set<UUID> typeUuIds = new HashSet<>();

    // Audit Fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "last_modified_at", nullable = false)
    private OffsetDateTime lastModifiedAt;

    @Column(name = "last_modified_by", nullable = false)
    private String lastModifiedBy;

    public TypeListEntity() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public UUID getBusinessUuid() { return businessUuid; }
    public void setBusinessUuid(UUID businessUuid) { this.businessUuid = businessUuid; }

    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }

    public boolean isSoftDeleted() { return softDeleted; }
    public void setSoftDeleted(boolean softDeleted) { this.softDeleted = softDeleted; }

    public Set<UUID> getTypeUuIds() { return typeUuIds; }
    public void setTypeUuIds(Set<UUID> typeUuIds) { this.typeUuIds = typeUuIds; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getLastModifiedAt() { return lastModifiedAt; }
    public void setLastModifiedAt(OffsetDateTime lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class LifecycleStateEntity {

    @Column(name = "is_archived", nullable = false)
    private boolean archived = false;

    @Column(name = "is_soft_deleted", nullable = false)
    private boolean softDeleted = false;

    // JPA Requirement
    public LifecycleStateEntity() {}

    // Getters and Setters
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
    public boolean isSoftDeleted() { return softDeleted; }
    public void setSoftDeleted(boolean softDeleted) { this.softDeleted = softDeleted; }
}

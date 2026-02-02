package com.github.calhanwynters.dashboard_admin_lead.common;

import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.domain.AbstractAggregateRoot;

@MappedSuperclass
public abstract class BaseAggregateRoot<T extends BaseAggregateRoot<T>>
        extends AbstractAggregateRoot<T> {

    @Embedded
    protected AuditMetadata auditMetadata;

    protected BaseAggregateRoot(AuditMetadata auditMetadata) {
        // If null (new entity), we expect the factory to provide the initial actor
        this.auditMetadata = auditMetadata;
    }

    protected BaseAggregateRoot() {} // JPA Requirement

    protected void recordUpdate(Actor actor) {
        this.auditMetadata = this.auditMetadata.update(actor);
    }

    public AuditMetadata getAuditMetadata() {
        return auditMetadata;
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.common;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.validationchecks.DomainGuard;

/**
 * Composed Value Object grouping temporal audit data.
 * Enforces cross-field invariants between creation and modification.
 */
public record AuditMetadata(CreatedAt createdAt, LastModified lastModified) {

    /**
     * Compact constructor for 2026 Edition.
     */
    public AuditMetadata {
        // 1. Existence Checks
        DomainGuard.notNull(createdAt, "Created At");
        DomainGuard.notNull(lastModified, "Last Modified");

        // 2. Semantic Integrity: CreatedAt <= LastModified
        // Enforces the core business rule that an object cannot be modified before it is created.
        DomainGuard.ensure(
                !lastModified.value().isBefore(createdAt.value()),
                "Last modified date (%s) cannot be earlier than creation date (%s)."
                        .formatted(lastModified.value(), createdAt.value()),
                "VAL-016", "TEMPORAL_INTEGRITY"
        );
    }

    /**
     * Factory for new entities.
     * In 2026, we initialize both to the same instant for a new record.
     */
    public static AuditMetadata create() {
        CreatedAt now = CreatedAt.now();
        // Both are born at the same microsecond
        return new AuditMetadata(now, new LastModified(now.value()));
    }


    /**
     * Produces a new metadata instance for an update event.
     * Keeps the original CreatedAt but refreshes LastModified to 'now'.
     */
    public AuditMetadata update() {
        return new AuditMetadata(this.createdAt, LastModified.now());
    }
}

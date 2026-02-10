package com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import jakarta.persistence.Embedded;
import jakarta.persistence.MappedSuperclass;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.util.Optional;

@MappedSuperclass
public abstract class BaseAggregateRoot<T extends BaseAggregateRoot<T>>
        extends AbstractAggregateRoot<T> {

    @Embedded
    protected AuditMetadata auditMetadata;

    protected BaseAggregateRoot(AuditMetadata auditMetadata) {
        this.auditMetadata = auditMetadata;
    }

    protected BaseAggregateRoot() {} // JPA Requirement

    /**
     * The core helper for the "Two-Liner" pattern.
     * Manages the lifecycle of a domain action: Mutation -> Audit -> Event.
     *
     * @param actor    The admin performing the action.
     * @param event    The domain event to register (can be null).
     * @param mutation The logic to update internal state.
     */
    protected void applyChange(Actor actor, Object event, Runnable mutation) {
        DomainGuard.notNull(actor, "Actor performing the action");

        // 1. Execute the state change (The mutation)
        Optional.ofNullable(mutation).ifPresent(Runnable::run);

        // 2. Automatically stamp the audit metadata
        this.recordUpdate(actor);

        // 3. Register the event for Spring Data to publish
        Optional.ofNullable(event).ifPresent(this::registerEvent);
    }

    protected void recordUpdate(Actor actor) {
        this.auditMetadata = this.auditMetadata.update(actor);
    }

    public AuditMetadata getAuditMetadata() {
        return auditMetadata;
    }
}

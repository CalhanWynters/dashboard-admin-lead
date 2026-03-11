package com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class BaseAggregateRoot<T extends BaseAggregateRoot<T>>
        extends AbstractAggregateRoot<T> {

    protected PkId id;
    protected UuId uuId;
    protected UuId businessUuId;
    protected Long optLockVer;
    protected Integer schemaVersion;
    protected AuditMetadata auditMetadata;
    protected LifecycleState lifecycleState;

    // NEW: Tracking Kafka Alignment
    protected OffsetDateTime lastSyncedAt;

    protected BaseAggregateRoot(PkId id, UuId uuId, UuId businessUuId,
                                AuditMetadata auditMetadata, Long optLockVer, Integer schemaVer,
                                OffsetDateTime lastSyncedAt) {
        this.id = id;
        this.uuId = uuId;
        this.businessUuId = businessUuId;
        this.auditMetadata = auditMetadata;
        this.optLockVer = optLockVer;
        this.schemaVersion = (schemaVer != null) ? schemaVer : 1;
        this.lastSyncedAt = lastSyncedAt;
    }

    protected BaseAggregateRoot() {}

    // --- KAFKA ALIGNMENT LOGIC ---

    /**
     * Updates the sync timestamp. Call this inside syncToKafka's mutation lambda.
     */
    protected void recordSync() {
        this.lastSyncedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Checks if the DB has been modified since the last Kafka sync.
     * Useful for showing "Sync Required" badges in the Admin UI.
     */
    public boolean isSyncPending() {
        if (lastSyncedAt == null) return true;
        return auditMetadata.lastModified().value().isAfter(lastSyncedAt);
    }

    // --- GENERIC ENGINE ---

    protected <V> void applyDomainChange(Actor actor, V newValue, BiFunction<V, Actor, V> validator,
                                         Function<V, Object> eventFactory, Consumer<V> mutation) {
        ensureActive();
        V validatedValue = validator.apply(newValue, actor);

        // Potential No-Op optimization could be added here
        this.applyChange(actor, eventFactory.apply(validatedValue), () -> mutation.accept(validatedValue));
    }

    // For the methods below, have child aggregates call domain event and apply change. verifying authority and SOC 2 requirements on this base aggregate.
        // syncToKafka
        // public void updateBusinessUuId
        // archive
        // unarchive
        // softDelete
        // restore
        // hardDelete

    protected void ensureActive() {
        if (this.lifecycleState != null && this.lifecycleState.softDeleted()) {
            throw new IllegalStateException("Action not allowed on a soft-deleted aggregate.");
        }
    }

    protected void applyChange(Actor actor, Object event, Runnable mutation) {
        DomainGuard.notNull(actor, "Actor");
        Optional.ofNullable(mutation).ifPresent(Runnable::run);
        this.recordUpdate(actor);
        Optional.ofNullable(event).ifPresent(this::registerEvent);
    }

    protected void recordUpdate(Actor actor) {
        this.auditMetadata = this.auditMetadata.update(actor);
    }

    // --- GETTERS ---
    public PkId getId() { return id; }
    public UuId getUuId() { return uuId; }
    public UuId getBusinessUuId() { return businessUuId; }
    public Long getOptLockVer() { return optLockVer; }
    public Integer getSchemaVersion() { return schemaVersion; }
    public OffsetDateTime getLastSyncedAt() { return lastSyncedAt; }
    public AuditMetadata getAuditMetadata() { return auditMetadata; }
}

package com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.PkId;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import org.springframework.data.domain.AbstractAggregateRoot;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Standardized Base for all Product-related Aggregates.
 * Enforces SOC 2 Authorizations, Audit Integrity, and Lifecycle Guards.
 */
public abstract class BaseAggregateRoot<T extends BaseAggregateRoot<T>>
        extends AbstractAggregateRoot<T> {

    protected PkId id;
    protected UuId uuId;
    protected UuId businessUuId;
    protected Long optLockVer;
    protected Integer schemaVersion;
    protected AuditMetadata auditMetadata;
    protected LifecycleState lifecycleState;
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

    // --- GENERIC ENGINE & ORCHESTRATORS ---

    /**
     * Handles generic field updates (e.g., Rename, Description).
     */
    protected <V> void applyDomainChange(Actor actor, V newValue, BiFunction<V, Actor, V> validator,
                                         Function<V, Object> eventFactory, Consumer<V> mutation) {
        ensureActive();
        V validatedValue = validator.apply(newValue, actor);
        this.applyChange(actor, eventFactory.apply(validatedValue), () -> mutation.accept(validatedValue));
    }

    /**
     * Standardized Sync orchestration.
     */
    public void executeSync(Actor actor, Function<Actor, Object> eventFactory) {
        ensureActive();
        verifySyncAuthority(actor);
        this.applyChange(actor, eventFactory.apply(actor), this::recordSync);
    }

    /**
     * Standardized Business ID Change.
     */
    public void executeBusinessUuIdUpdate(UuId newId, Actor actor, Function<UuId, Object> eventFactory) {
        ensureActive();
        UuId validatedId = evaluateBusinessIdChange(this.businessUuId, newId, actor);
        this.applyChange(actor, eventFactory.apply(validatedId), () -> this.businessUuId = validatedId);
    }

    // --- LIFECYCLE ACTIONS ---

    public void executeArchive(Actor actor, Object event) {
        verifyLifecycleAuthority(actor);
        this.applyChange(actor, event, () -> this.lifecycleState = this.lifecycleState.withArchived(true));
    }

    public void executeUnarchive(Actor actor, Object event) {
        verifyLifecycleAuthority(actor);
        this.applyChange(actor, event, () -> this.lifecycleState = this.lifecycleState.withArchived(false));
    }

    public void executeSoftDelete(Actor actor, Object event) {
        ensureActive();
        verifyLifecycleAuthority(actor);
        this.applyChange(actor, event, () -> this.lifecycleState = this.lifecycleState.withSoftDeleted(true));
    }

    public void executeRestore(Actor actor, Object event) {
        if (!this.lifecycleState.softDeleted()) return;
        verifyRestorable(actor);
        this.applyChange(actor, event, () -> this.lifecycleState = this.lifecycleState.withSoftDeleted(false));
    }

    public void executeHardDelete(Actor actor, Object event) {
        verifyHardDeleteAuthority(actor);
        this.applyChange(actor, event, null);
    }

    // --- GUARDS & AUDIT ---

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

    protected void recordSync() {
        this.lastSyncedAt = OffsetDateTime.now(ZoneOffset.UTC);
    }

    public boolean isSyncPending() {
        if (lastSyncedAt == null) return true;
        return auditMetadata.lastModified().value().isAfter(lastSyncedAt);
    }

    // --- SOC 2 STATIC VALIDATIONS ---

    public static void verifySyncAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Data sync requires Manager or Admin roles.", "SEC-403", actor);
        }
    }

    public static void verifyLifecycleAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_MANAGER) && !actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Lifecycle actions require Manager or Admin roles.", "SEC-403", actor);
        }
    }

    public static void verifyRestorable(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Restoration is restricted to Administrators.", "SEC-403", actor);
        }
    }

    public static void verifyHardDeleteAuthority(Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Hard deletes are restricted to Administrators.", "SEC-001", actor);
        }
    }

    public static UuId evaluateBusinessIdChange(UuId current, UuId next, Actor actor) {
        if (!actor.hasRole(Actor.ROLE_ADMIN)) {
            throw new DomainAuthorizationException("Business ID changes restricted to Admin.", "SEC-401", actor);
        }
        DomainGuard.notNull(next, "New Business UUID");
        if (current.equals(next)) throw new IllegalArgumentException("New ID must be different.");
        return next;
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

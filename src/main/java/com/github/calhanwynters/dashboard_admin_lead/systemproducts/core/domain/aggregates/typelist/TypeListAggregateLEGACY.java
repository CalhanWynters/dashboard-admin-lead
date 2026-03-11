package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.LEGACYBaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleansLEGACY;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.events.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TypeListAggregateLEGACY extends LEGACYBaseAggregateRoot<TypeListAggregateLEGACY> {

    private final TypeListId typeListId;
    private final TypeListUuId typeListUuId;
    private TypeListBusinessUuId typeListBusinessUuId;
    private final Set<TypesUuId> typeUuIds;
    private ProductBooleansLEGACY productBooleansLEGACY; // Corrected: No more primitive boolean
    // Add Version-Based Optimistic Locking "optLockVer"
    // Add Schema-Based Versioning "schemaVer"


    public TypeListAggregateLEGACY(TypeListId typeListId,
                                   TypeListUuId typeListUuId,
                                   TypeListBusinessUuId typeListBusinessUuId,
                                   Set<TypesUuId> typeUuIds,
                                   ProductBooleansLEGACY productBooleansLEGACY, // Replaced boolean
                                   AuditMetadata auditMetadata) {
        super(auditMetadata);
        this.typeListId = typeListId;
        this.typeListUuId = DomainGuard.notNull(typeListUuId, "TypeList UUID");
        this.typeListBusinessUuId = DomainGuard.notNull(typeListBusinessUuId, "Business UUID");
        this.typeUuIds = new HashSet<>(typeUuIds != null ? typeUuIds : Collections.emptySet());
        this.productBooleansLEGACY = productBooleansLEGACY != null ? productBooleansLEGACY : new ProductBooleansLEGACY(false, false);
    }

    public static TypeListAggregateLEGACY create(TypeListUuId uuId, TypeListBusinessUuId bUuId, Actor actor) {
        TypeListBehavior.verifyCreationAuthority(actor);

        TypeListAggregateLEGACY aggregate = new TypeListAggregateLEGACY(
                null, uuId, bUuId, new HashSet<>(), new ProductBooleansLEGACY(false, false), AuditMetadata.create(actor)
        );
        aggregate.registerEvent(new TypeListCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void updateBusinessUuId(TypeListBusinessUuId newId, Actor actor) {
        TypeListBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());

        // Validate using your existing logic (Admin-only, non-null, difference check)
        var validatedId = TypeListBehavior.evaluateBusinessIdChange(this.typeListBusinessUuId, newId, actor);

        this.applyChange(actor,
                new TypeListBusinessUuIdChangedEvent(typeListUuId, this.typeListBusinessUuId, validatedId, actor),
                () -> this.typeListBusinessUuId = validatedId);
    }

    public void syncToKafka(Actor actor) {
        TypeListBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        TypeListBehavior.verifySyncAuthority(actor);

        this.applyChange(actor, new TypeListDataSyncedEvent(typeListUuId, typeListBusinessUuId, typeUuIds, productBooleansLEGACY, actor), null);
    }

    public void attachType(TypesUuId typeUuId, Actor actor) {
        TypeListBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        TypeListBehavior.ensureCanAttach(this.typeUuIds, typeUuId, actor);

        this.applyChange(actor,
                new TypeAttachedEvent(this.typeListUuId, typeUuId, actor),
                () -> this.typeUuIds.add(typeUuId)
        );
    }

    public void detachType(TypesUuId typeUuId, Actor actor) {
        TypeListBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        TypeListBehavior.ensureCanDetach(this.typeUuIds, typeUuId, actor);

        this.applyChange(actor,
                new TypeDetachedEvent(this.typeListUuId, typeUuId, actor),
                () -> this.typeUuIds.remove(typeUuId)
        );
    }

    public void clearAllTypes(Actor actor) {
        TypeListBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        TypeListBehavior.verifyMembershipAuthority(actor);

        if (this.typeUuIds.isEmpty()) return;

        this.applyChange(actor,
                new TypeListClearedEvent(this.typeListUuId, actor),
                this.typeUuIds::clear
        );
    }

    public void softDelete(Actor actor) {
        TypeListBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        TypeListBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new TypeListSoftDeletedEvent(this.typeListUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(this.productBooleansLEGACY.archived(), true)
        );
    }

    public void hardDelete(Actor actor) {
        TypeListBehavior.verifyLifecycleAuthority(actor);
        this.applyChange(actor, new TypeListHardDeletedEvent(this.typeListUuId, actor), null);
    }

    public void restore(Actor actor) {
        if (!this.productBooleansLEGACY.softDeleted()) return;
        TypeListBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new TypeListRestoredEvent(this.typeListUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(this.productBooleansLEGACY.archived(), false)
        );
    }

    public void archive(Actor actor) {
        // Line 1: Auth
        TypeListBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record instance)
        this.applyChange(actor,
                new TypeListArchivedEvent(this.typeListUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(true, this.productBooleansLEGACY.softDeleted())
        );
    }

    public void unarchive(Actor actor) {
        // Line 1: Auth
        TypeListBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record instance)
        this.applyChange(actor,
                new TypeListUnarchivedEvent(this.typeListUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(false, this.productBooleansLEGACY.softDeleted())
        );
    }


    // --- ACCESSORS ---
    public boolean isDeleted() { return productBooleansLEGACY.softDeleted(); }
    public ProductBooleansLEGACY getProductBooleans() { return productBooleansLEGACY; }
    public TypeListId getTypeListId() { return typeListId; }
    public TypeListUuId getTypeListUuId() { return typeListUuId; }
    public TypeListBusinessUuId getTypeListBusinessUuId() { return typeListBusinessUuId; }
    public Set<TypesUuId> getTypeUuIds() { return Collections.unmodifiableSet(typeUuIds); }
}

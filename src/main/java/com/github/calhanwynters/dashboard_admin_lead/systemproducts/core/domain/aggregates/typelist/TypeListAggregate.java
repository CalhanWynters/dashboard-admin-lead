package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesBehavior;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.events.FeatureDataSyncedEvent;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.events.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.types.TypesDomainWrapper.TypesUuId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TypeListAggregate extends BaseAggregateRoot<TypeListAggregate> {

    private final TypeListId typeListId;
    private final TypeListUuId typeListUuId;
    private final TypeListBusinessUuId typeListBusinessUuId;
    private final Set<TypesUuId> typeUuIds;
    private ProductBooleans productBooleans; // Corrected: No more primitive boolean

    public TypeListAggregate(TypeListId typeListId,
                             TypeListUuId typeListUuId,
                             TypeListBusinessUuId typeListBusinessUuId,
                             Set<TypesUuId> typeUuIds,
                             ProductBooleans productBooleans, // Replaced boolean
                             AuditMetadata auditMetadata) {
        super(auditMetadata);
        this.typeListId = typeListId;
        this.typeListUuId = DomainGuard.notNull(typeListUuId, "TypeList UUID");
        this.typeListBusinessUuId = DomainGuard.notNull(typeListBusinessUuId, "Business UUID");
        this.typeUuIds = new HashSet<>(typeUuIds != null ? typeUuIds : Collections.emptySet());
        this.productBooleans = productBooleans != null ? productBooleans : new ProductBooleans(false, false);
    }

    public static TypeListAggregate create(TypeListUuId uuId, TypeListBusinessUuId bUuId, Actor actor) {
        TypeListBehavior.verifyCreationAuthority(actor);

        TypeListAggregate aggregate = new TypeListAggregate(
                null, uuId, bUuId, new HashSet<>(), new ProductBooleans(false, false), AuditMetadata.create(actor)
        );
        aggregate.registerEvent(new TypeListCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    // Need a 2-liner pattern method for TypeListUpdateBusUuIdCommand

    // Need a 2-liner pattern method for TypeListTrunkDataOut
    public void syncToKafka(Actor actor) {
        TypeListBehavior.ensureActive(this.productBooleans.softDeleted());
        TypeListBehavior.verifySyncAuthority(actor);

        this.applyChange(actor, new TypeListDataSyncedEvent(typeListUuId, typeListBusinessUuId, typeUuIds, productBooleans, actor), null);
    }

    public void attachType(TypesUuId typeUuId, Actor actor) {
        TypeListBehavior.ensureActive(this.productBooleans.softDeleted());
        TypeListBehavior.ensureCanAttach(this.typeUuIds, typeUuId, actor);

        this.applyChange(actor,
                new TypeAttachedEvent(this.typeListUuId, typeUuId, actor),
                () -> this.typeUuIds.add(typeUuId)
        );
    }

    public void detachType(TypesUuId typeUuId, Actor actor) {
        TypeListBehavior.ensureActive(this.productBooleans.softDeleted());
        TypeListBehavior.ensureCanDetach(this.typeUuIds, typeUuId, actor);

        this.applyChange(actor,
                new TypeDetachedEvent(this.typeListUuId, typeUuId, actor),
                () -> this.typeUuIds.remove(typeUuId)
        );
    }

    public void clearAllTypes(Actor actor) {
        TypeListBehavior.ensureActive(this.productBooleans.softDeleted());
        TypeListBehavior.verifyMembershipAuthority(actor);

        if (this.typeUuIds.isEmpty()) return;

        this.applyChange(actor,
                new TypeListClearedEvent(this.typeListUuId, actor),
                this.typeUuIds::clear
        );
    }

    public void softDelete(Actor actor) {
        TypeListBehavior.ensureActive(this.productBooleans.softDeleted());
        TypeListBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new TypeListSoftDeletedEvent(this.typeListUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), true)
        );
    }

    public void hardDelete(Actor actor) {
        TypeListBehavior.verifyLifecycleAuthority(actor);
        this.applyChange(actor, new TypeListHardDeletedEvent(this.typeListUuId, actor), null);
    }

    public void restore(Actor actor) {
        if (!this.productBooleans.softDeleted()) return;
        TypeListBehavior.verifyLifecycleAuthority(actor);

        this.applyChange(actor,
                new TypeListRestoredEvent(this.typeListUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), false)
        );
    }

    public void archive(Actor actor) {
        // Line 1: Auth
        TypeListBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record instance)
        this.applyChange(actor,
                new TypeListArchivedEvent(this.typeListUuId, actor),
                () -> this.productBooleans = new ProductBooleans(true, this.productBooleans.softDeleted())
        );
    }

    public void unarchive(Actor actor) {
        // Line 1: Auth
        TypeListBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record instance)
        this.applyChange(actor,
                new TypeListUnarchivedEvent(this.typeListUuId, actor),
                () -> this.productBooleans = new ProductBooleans(false, this.productBooleans.softDeleted())
        );
    }


    // --- ACCESSORS ---
    public boolean isDeleted() { return productBooleans.softDeleted(); }
    public ProductBooleans getProductBooleans() { return productBooleans; }
    public TypeListId getTypeListId() { return typeListId; }
    public TypeListUuId getTypeListUuId() { return typeListUuId; }
    public TypeListBusinessUuId getTypeListBusinessUuId() { return typeListBusinessUuId; }
    public Set<TypesUuId> getTypeUuIds() { return Collections.unmodifiableSet(typeUuIds); }
}

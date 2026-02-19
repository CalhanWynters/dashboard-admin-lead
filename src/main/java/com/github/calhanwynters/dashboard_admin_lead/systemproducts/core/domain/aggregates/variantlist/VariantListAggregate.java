package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.events.*;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variants.VariantsDomainWrapper.VariantsUuId;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Aggregate Root for managing collections of Variants.
 * Follows Two-Liner Pattern with mandatory SOC 2 Authorizations.
 */
public class VariantListAggregate extends BaseAggregateRoot<VariantListAggregate> {

    private final VariantListId variantListId;
    private final VariantListUuId variantListUuId;
    private final Set<VariantsUuId> variantUuIds;
    private ProductBooleans productBooleans; // Replaced boolean deleted

    public VariantListAggregate(VariantListId variantListId,
                                VariantListUuId variantListUuId,
                                VariantListBusinessUuId variantListBusinessUuId,
                                Set<VariantsUuId> variantUuIds,
                                ProductBooleans productBooleans, // Updated parameter
                                AuditMetadata auditMetadata) {
        super(auditMetadata);
        this.variantListId = variantListId;
        this.variantListUuId = DomainGuard.notNull(variantListUuId, "VariantList UUID");
        DomainGuard.notNull(variantListBusinessUuId, "Business UUID");
        this.variantUuIds = new HashSet<>(variantUuIds != null ? variantUuIds : Collections.emptySet());
        // Defaulting to false/false if null is passed
        this.productBooleans = productBooleans != null ? productBooleans : new ProductBooleans(false, false);
    }

    public static VariantListAggregate create(VariantListUuId uuId, VariantListBusinessUuId bUuId, Actor actor) {
        VariantListBehavior.verifyCreationAuthority(actor);

        VariantListAggregate aggregate = new VariantListAggregate(
                null, uuId, bUuId, new HashSet<>(), new ProductBooleans(false, false), AuditMetadata.create(actor)
        );
        aggregate.registerEvent(new VariantListCreatedEvent(uuId, bUuId, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    // Need a 2-liner pattern method for VariantListUpdateBusUuIdCommand
    // Need a 2-liner pattern method for VariantListTrunkDataOut

    public void attachVariant(VariantsUuId variantUuId, Actor actor) {
        VariantListBehavior.ensureActive(this.productBooleans.softDeleted());
        VariantListBehavior.ensureCanAttach(this.variantUuIds, variantUuId, actor);

        this.applyChange(actor,
                new VariantAttachedEvent(this.variantListUuId, variantUuId, actor),
                () -> this.variantUuIds.add(variantUuId)
        );
    }

    public void detachVariant(VariantsUuId variantUuId, Actor actor) {
        VariantListBehavior.ensureActive(this.productBooleans.softDeleted());
        VariantListBehavior.ensureCanDetach(this.variantUuIds, variantUuId, actor);

        this.applyChange(actor,
                new VariantDetachedEvent(this.variantListUuId, variantUuId, actor),
                () -> this.variantUuIds.remove(variantUuId)
        );
    }

    public void reorder(Actor actor) {
        VariantListBehavior.ensureActive(this.productBooleans.softDeleted());
        VariantListBehavior.ensureCanReorder(this.variantUuIds, actor);

        this.applyChange(actor, new VariantListReorderedEvent(this.variantListUuId, actor), null);
    }

    public void clearAllVariants(Actor actor) {
        VariantListBehavior.ensureActive(this.productBooleans.softDeleted());
        VariantListBehavior.verifyMembershipAuthority(actor);

        if (this.variantUuIds.isEmpty()) return;

        this.applyChange(actor,
                new VariantListClearedEvent(this.variantListUuId, actor),
                this.variantUuIds::clear
        );
    }

    public void softDelete(Actor actor) {
        VariantListBehavior.ensureActive(this.productBooleans.softDeleted());
        VariantListBehavior.verifyLifecycleAuthority(actor);

        // Replace the record to change softDeleted to true
        this.applyChange(actor,
                new VariantListSoftDeletedEvent(this.variantListUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), true)
        );
    }

    public void restore(Actor actor) {
        if (!this.productBooleans.softDeleted()) return;
        VariantListBehavior.verifyLifecycleAuthority(actor);

        // Replace the record to change softDeleted to false
        this.applyChange(actor,
                new VariantListRestoredEvent(this.variantListUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), false)
        );
    }

    public void hardDelete(Actor actor) {
        VariantListBehavior.verifyLifecycleAuthority(actor);
        this.applyChange(actor, new VariantListHardDeletedEvent(this.variantListUuId, actor), null);
    }

    public void archive(Actor actor) {
        // Line 1: Auth
        VariantListBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record to set archived = true)
        this.applyChange(actor,
                new VariantListArchivedEvent(this.variantListUuId, actor),
                () -> this.productBooleans = new ProductBooleans(true, this.productBooleans.softDeleted())
        );
    }

    public void unarchive(Actor actor) {
        // Line 1: Auth
        VariantListBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record to set archived = false)
        this.applyChange(actor,
                new VariantListUnarchivedEvent(this.variantListUuId, actor),
                () -> this.productBooleans = new ProductBooleans(false, this.productBooleans.softDeleted())
        );
    }

    // --- ACCESSORS ---
    public ProductBooleans getProductBooleans() { return productBooleans; }
    public boolean isDeleted() { return productBooleans.softDeleted(); }
    public VariantListId getVariantListId() { return variantListId; }
    public VariantListUuId getVariantListUuId() { return variantListUuId; }
    public Set<VariantsUuId> getVariantUuIds() { return Collections.unmodifiableSet(variantUuIds); }
}

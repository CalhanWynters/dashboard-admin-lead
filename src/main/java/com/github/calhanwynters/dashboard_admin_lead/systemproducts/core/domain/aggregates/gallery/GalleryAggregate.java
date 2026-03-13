package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.events.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.ImageUuId;

/**
 * Modernized Gallery Aggregate.
 * Manages collections of ImageUuIds and visibility state.
 */
public class GalleryAggregate extends BaseAggregateRoot<GalleryAggregate, GalleryId, GalleryUuId, GalleryBusinessUuId> {

    private final List<ImageUuId> imageUuIds = new ArrayList<>();
    private boolean isPublic;

    public GalleryAggregate(GalleryId id, GalleryUuId uuId, GalleryBusinessUuId businessUuId,
                            boolean isPublic, List<ImageUuId> imageUuIds,
                            AuditMetadata auditMetadata, LifecycleState lifecycleState,
                            Long optLockVer, Integer schemaVer, OffsetDateTime lastSyncedAt) {
        super(id, uuId, businessUuId, auditMetadata, optLockVer, schemaVer, lastSyncedAt);
        this.isPublic = isPublic;
        if (imageUuIds != null) {
            this.imageUuIds.addAll(imageUuIds);
        }
        this.lifecycleState = lifecycleState;
    }

    // --- FACTORY ---

    public static GalleryAggregate create(GalleryUuId galleryUuId, GalleryBusinessUuId businessUuId, Actor actor) {
        // Delegate to Behavior for role checks and non-null guards
        GalleryBehavior.validateCreation(galleryUuId, businessUuId, actor);

        GalleryAggregate aggregate = new GalleryAggregate(
                null, galleryUuId, businessUuId, false, List.of(),
                AuditMetadata.create(actor), new LifecycleState(false, false),
                0L, 1, null
        );

        // Register the "Created" event
        aggregate.registerEvent(new GalleryCreatedEvent(galleryUuId, businessUuId, actor));

        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void updateBusinessUuId(GalleryBusinessUuId newId, Actor actor) {
        this.executeBusinessUuIdUpdate(newId, actor,
                val -> new GalleryBusinessUuIdChangedEvent(this.uuId, this.businessUuId, val, actor)
        );
    }

    public void togglePublicStatus(boolean newStatus, Actor actor) {
        this.applyDomainChange(actor, newStatus,
                (next, auth) -> GalleryBehavior.evaluatePublicityChange(this.isPublic, next, auth),
                val -> new GalleryPublicStatusToggledEvent(this.uuId, val, actor),
                val -> this.isPublic = val
        );
    }

    public void addImage(ImageUuId imageUuId, Actor actor) {
        this.applyDomainChange(actor, imageUuId,
                (next, auth) -> GalleryBehavior.evaluateImageAddition(next, this.imageUuIds.size(), auth),
                val -> new ImageAddedToGalleryEvent(this.uuId, val, actor),
                this.imageUuIds::add
        );
    }

    public void removeImage(ImageUuId imageUuId, Actor actor) {
        this.applyDomainChange(actor, imageUuId,
                (next, auth) -> GalleryBehavior.evaluateImageRemoval(next, this.imageUuIds.contains(next), auth),
                val -> new ImageRemovedFromGalleryEvent(this.uuId, val, actor),
                this.imageUuIds::remove
        );
    }

    public void syncToKafka(Actor actor) {
        this.executeSync(actor,
                auth -> new GalleryDataSyncedEvent(this.uuId, this.businessUuId, this.isPublic, this.lifecycleState, auth)
        );
    }

    // --- LIFECYCLE (Standardized) ---
    public void archive(Actor actor) { this.executeArchive(actor, new GalleryArchivedEvent(this.uuId, actor)); }
    public void unarchive(Actor actor) { this.executeUnarchive(actor, new GalleryUnarchivedEvent(this.uuId, actor)); }
    public void softDelete(Actor actor) { this.executeSoftDelete(actor, new GallerySoftDeletedEvent(this.uuId, actor)); }
    public void restore(Actor actor) { this.executeRestore(actor, new GalleryRestoredEvent(this.uuId, actor)); }
    public void hardDelete(Actor actor) { this.executeHardDelete(actor, new GalleryHardDeletedEvent(this.uuId, actor)); }

    // --- GETTERS ---
    public List<ImageUuId> getImageUuIds() { return List.copyOf(imageUuIds); }
    public boolean isPublic() { return isPublic; }
    public LifecycleState getLifecycleState() { return lifecycleState; }
}

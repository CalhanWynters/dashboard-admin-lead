package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.events.*;

import java.time.OffsetDateTime;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

/**
 * Modernized Image Aggregate.
 * Uses the Generic Engine for SOC 2 compliance and boilerplate reduction.
 */
public class ImageAggregate extends BaseAggregateRoot<ImageAggregate, ImageId, ImageUuId, ImagesBusinessUuId> {

    private ImageName imageName;
    private ImageDescription imageDescription;
    private ImageUrl imageUrl;

    public ImageAggregate(ImageId id, ImageUuId uuId, ImagesBusinessUuId businessUuId,
                          ImageName name, ImageDescription description, ImageUrl url,
                          AuditMetadata auditMetadata, LifecycleState lifecycleState,
                          Long optLockVer, Integer schemaVer, OffsetDateTime lastSyncedAt) {
        super(id, uuId, businessUuId, auditMetadata, optLockVer, schemaVer, lastSyncedAt);
        this.imageName = name;
        this.imageDescription = description;
        this.imageUrl = url;
        this.lifecycleState = lifecycleState;
    }

    // --- FACTORY ---

    public static ImageAggregate create(ImageUuId uuId, ImagesBusinessUuId bUuId, ImageName name,
                                        ImageDescription desc, ImageUrl url, Actor actor) {

        // Use the centralized behavior check (once you've added the 3-arg method to ImagesBehavior)
        ImagesBehavior.validateCreation(uuId, bUuId, actor);

        ImageAggregate aggregate = new ImageAggregate(
                null, uuId, bUuId, name, desc, url,
                AuditMetadata.create(actor), new LifecycleState(false, false),
                0L, 1, null
        );

        aggregate.registerEvent(new ImageUploadedEvent(uuId, url, actor));
        return aggregate;
    }

    // --- DOMAIN ACTIONS ---

    public void updateBusinessUuId(ImagesBusinessUuId newId, Actor actor) {
        this.executeBusinessUuIdUpdate(newId, actor,
                val -> new ImageBusinessUuIdChangedEvent(this.uuId, this.businessUuId, val, actor)
        );
    }

    public void rename(ImageName newName, Actor actor) {
        this.applyDomainChange(actor, newName,
                (next, auth) -> ImagesBehavior.evaluateRename(this.imageName, next, auth),
                val -> new ImageNameUpdatedEvent(this.uuId, val, actor),
                val -> this.imageName = val
        );
    }

    public void updateDescription(ImageDescription newDescription, Actor actor) {
        this.applyDomainChange(actor, newDescription,
                (next, auth) -> ImagesBehavior.evaluateDescriptionUpdate(this.imageDescription, next, auth),
                val -> new ImageDescriptionUpdatedEvent(this.uuId, val, actor),
                val -> this.imageDescription = val
        );
    }

    public void updateUrl(ImageUrl newUrl, Actor actor) {
        this.applyDomainChange(actor, newUrl,
                (next, auth) -> ImagesBehavior.evaluateUrlUpdate(this.imageUrl, next, auth),
                val -> new ImageUrlUpdatedEvent(this.uuId, val, actor),
                val -> this.imageUrl = val
        );
    }

    public void syncToKafka(Actor actor) {
        this.executeSync(actor,
                auth -> new ImageDataSyncedEvent(this.uuId, this.businessUuId, this.imageName,
                        this.imageDescription, this.imageUrl, this.lifecycleState, auth)
        );
    }

    // --- LIFECYCLE (Standardized) ---
    public void archive(Actor actor) { this.executeArchive(actor, new ImageArchivedEvent(this.uuId, actor)); }
    public void unarchive(Actor actor) { this.executeUnarchive(actor, new ImageUnarchivedEvent(this.uuId, actor)); }
    public void softDelete(Actor actor) { this.executeSoftDelete(actor, new ImageSoftDeletedEvent(this.uuId, actor)); }
    public void restore(Actor actor) { this.executeRestore(actor, new ImageRestoredEvent(this.uuId, actor)); }
    public void hardDelete(Actor actor) { this.executeHardDelete(actor, new ImageHardDeletedEvent(this.uuId, actor)); }

    // --- GETTERS ---
    public ImageName getImageName() { return imageName; }
    public ImageDescription getImageDescription() { return imageDescription; }
    public ImageUrl getImageUrl() { return imageUrl; }
    public LifecycleState getLifecycleState() { return lifecycleState; }
}

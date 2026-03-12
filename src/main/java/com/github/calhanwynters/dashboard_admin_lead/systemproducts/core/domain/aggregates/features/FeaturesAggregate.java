package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.events.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.features.FeaturesDomainWrapper.*;

import java.time.OffsetDateTime;

/**
 * Cleaned FeaturesAggregate using Generic Base.
 * No more manual casts required for IDs.
 */
public class FeaturesAggregate extends BaseAggregateRoot<
        FeaturesAggregate,
        FeatureId,
        FeatureUuId,
        FeatureBusinessUuId
        > {

    private FeatureName featuresName;
    private FeatureLabel compatibilityTag;

    // --- CONSTRUCTOR ---
    public FeaturesAggregate(FeatureId id, FeatureUuId uuId, FeatureBusinessUuId businessUuId,
                             FeatureName name, FeatureLabel tag, AuditMetadata auditMetadata,
                             LifecycleState lifecycleState, Long optLockVer, Integer schemaVer,
                             OffsetDateTime lastSyncedAt) {
        // Now super() correctly accepts these specific types
        super(id, uuId, businessUuId, auditMetadata, optLockVer, schemaVer, lastSyncedAt);
        this.featuresName = name;
        this.compatibilityTag = tag;
        this.lifecycleState = lifecycleState;
    }

    // --- FACTORY ---
    public static FeaturesAggregate create(FeatureUuId uuId, FeatureBusinessUuId bUuId,
                                           FeatureName name, FeatureLabel tag, Actor actor) {

        FeaturesBehavior.validateCreation(uuId, bUuId, name, tag, actor);

        FeaturesAggregate aggregate = new FeaturesAggregate(
                null, uuId, bUuId, name, tag, AuditMetadata.create(actor),
                new LifecycleState(false, false), 0L, 1, null
        );

        // Matches your record definition perfectly
        aggregate.registerEvent(new FeatureCreatedEvent(uuId, bUuId, actor));

        return aggregate;
    }


    // --- DOMAIN ACTIONS ---

    // Inside FeaturesAggregate.java

    public void rename(FeatureName newName, Actor actor) {
        this.applyDomainChange(
                actor,
                newName,
                // Pass current name so the validator can check for differences
                (next, auth) -> FeaturesBehavior.evaluateRename(this.featuresName, next, auth),
                val -> new FeatureNameUpdatedEvent(this.uuId, val, actor),
                val -> this.featuresName = val
        );
    }


    public void updateCompatibilityTag(FeatureLabel newTag, Actor actor) {
        this.applyDomainChange(
                actor,
                newTag,
                // Pass current tag so the validator can check for differences
                (next, auth) -> FeaturesBehavior.evaluateCompatibilityTagUpdate(this.compatibilityTag, next, auth),
                val -> new FeatureCompTagUpdatedEvent(this.uuId, this.compatibilityTag, val, actor),
                val -> this.compatibilityTag = val
        );
    }

    public void syncToKafka(Actor actor) {
        this.executeSync(actor,
                auth -> new FeatureDataSyncedEvent(this.uuId, this.businessUuId,
                        this.featuresName, this.compatibilityTag, this.lifecycleState, auth)
        );
    }

    public void updateBusinessUuId(FeatureBusinessUuId newId, Actor actor) {
        // Generic executeBusinessUuIdUpdate uses FeatureBusinessUuId automatically now
        this.executeBusinessUuIdUpdate(newId, actor,
                val -> new FeatureBusinessUuIdChangedEvent(this.uuId, this.businessUuId, val, actor)
        );
    }

    // --- LIFECYCLE (One-Liners) ---

    public void archive(Actor actor) { this.executeArchive(actor, new FeatureArchivedEvent(this.uuId, actor)); }
    public void softDelete(Actor actor) { this.executeSoftDelete(actor, new FeatureSoftDeletedEvent(this.uuId, actor)); }
    public void restore(Actor actor) { this.executeRestore(actor, new FeatureRestoredEvent(this.uuId, actor)); }

    // --- GETTERS ---

    public FeatureName getFeaturesName() { return featuresName; }
    public FeatureLabel getCompatibilityTag() { return compatibilityTag; }
}

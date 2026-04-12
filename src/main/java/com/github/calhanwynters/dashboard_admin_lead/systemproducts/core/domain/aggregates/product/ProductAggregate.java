package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.Region;
import com.github.calhanwynters.dashboard_admin_lead.common.StatusEnums;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.events.*;

import java.time.OffsetDateTime;

/**
 * Central Orchestrator for System Products, managing structural compositions and lifecycle.
 */
public class ProductAggregate extends BaseAggregateRoot<
        ProductAggregate,
        ProductId,
        ProductUuId,
        ProductBusinessUuId
        > {

    private final ProductVersion productVersion;
    private ProductStatus productStatus;
    private ProductRegion productRegion;
    private ProductManifest manifest;
    private ProductPhysicalSpecs physicalSpecs;
    private ProductThumbnailUrl productThumbnailUrl;

    private GalleryUuId galleryUuId;
    private VariantListUuId variantListUuId;
    private TypeListUuId typeListUuId;
    private PriceListUuId priceListUuId;

    public ProductAggregate(ProductId id, ProductUuId uuId, ProductBusinessUuId businessUuId,
                            ProductManifest manifest, ProductVersion version, ProductStatus status,
                            ProductRegion region, ProductPhysicalSpecs specs,
                            ProductThumbnailUrl thumbnail, GalleryUuId gallery,
                            VariantListUuId variants, TypeListUuId types, PriceListUuId prices,
                            AuditMetadata auditMetadata, LifecycleState lifecycleState,
                            Long optLockVer, Integer schemaVer, OffsetDateTime lastSyncedAt) {

        super(id, uuId, businessUuId, auditMetadata, optLockVer, schemaVer, lastSyncedAt);
        this.manifest = manifest;
        this.productVersion = version;
        this.productStatus = status;
        this.productRegion = (region != null) ? region : ProductRegion.from(Region.GLOBAL);
        this.physicalSpecs = (specs != null) ? specs : ProductPhysicalSpecs.NONE;
        this.productThumbnailUrl = thumbnail;

        this.galleryUuId = (gallery != null) ? gallery : GalleryUuId.NONE;
        this.variantListUuId = (variants != null) ? variants : VariantListUuId.NONE;
        this.typeListUuId = (types != null) ? types : TypeListUuId.NONE;
        this.priceListUuId = (prices != null) ? prices : PriceListUuId.NONE;

        this.lifecycleState = lifecycleState;

        ProductBehavior.validateComposition(this);
    }

    // --- FACTORY ---

    public static ProductAggregate create(ProductUuId uuId, ProductBusinessUuId bUuId,
                                          ProductManifest manifest, ProductThumbnailUrl thumbnail,
                                          ProductStatus status, ProductRegion region,
                                          GalleryUuId gallery, VariantListUuId variants,
                                          TypeListUuId types, PriceListUuId prices,
                                          ProductPhysicalSpecs specs,
                                          Actor actor) {

        ProductBehavior.validateCreation(uuId, bUuId, actor);

        ProductAggregate product = new ProductAggregate(
                null, uuId, bUuId, manifest, ProductVersion.INITIAL, status,
                region, specs, thumbnail, gallery, variants, types, prices,
                AuditMetadata.create(actor), new LifecycleState(false, false),
                0L, 1, null
        );

        product.registerEvent(new ProductCreatedEvent(uuId, bUuId, status, actor));
        return product;
    }

    // --- DOMAIN ACTIONS ---

    public void updateBusinessUuId(ProductBusinessUuId newId, Actor actor) {
        this.executeBusinessUuIdUpdate(newId, actor,
                val -> new ProductBusinessUuIdChangedEvent(this.uuId, this.businessUuId, val, actor)
        );
    }

    public void updateStatus(StatusEnums newStatus, Actor actor) {
        this.applyDomainChange(actor, ProductStatus.of(newStatus),
                (next, auth) -> ProductBehavior.evaluateStatusTransition(this.productStatus, next, auth),
                val -> new ProductStatusUpdatedEvent(this.uuId, this.productStatus, val, actor),
                val -> this.productStatus = val
        );
    }

    public void updateRegion(ProductRegion newRegion, Actor actor) {
        this.applyDomainChange(actor, ProductRegion.from(newRegion.value()), // Use .from() based on your record definition
                (next, auth) -> ProductBehavior.evaluateRegionTransition(this.productRegion, next, auth),
                val -> new ProductRegionUpdatedEvent(this.uuId, this.productRegion, val, actor),
                val -> this.productRegion = val
        );
    }


    public void recordMissingDependency(String dependencyType, UuId missingId, Actor actor) {
        // SOC 2: Verify authority (allows Actor.SYSTEM)
        ProductBehavior.ensureDependencyResolution(dependencyType, false, actor);

        // Side-Effect: Force status to DRAFT to prevent accidental publishing of broken product
        this.applyChange(actor,
                new ProductDependencyMissingEvent(this.uuId, dependencyType, missingId, actor),
                () -> this.productStatus = ProductStatus.DRAFT
        );
    }

    public void updateThumbnailUrl(ProductThumbnailUrl newUrl, Actor actor) {
        this.applyDomainChange(actor, newUrl,
                (next, auth) -> ProductBehavior.evaluateUrlUpdate(this.productThumbnailUrl, next, auth),
                val -> new ProductThumbnailUrlUpdatedEvent(this.uuId, val, actor),
                val -> this.productThumbnailUrl = val
        );
    }

    public void updateManifest(ProductManifest newManifest, Actor actor) {
        this.applyDomainChange(actor, newManifest,
                (next, auth) -> {
                    ProductBehavior.verifyManifestUpdateAuthority(auth);
                    ProductBehavior.validateManifest(next);
                    return next;
                },
                val -> new ProductManifestUpdatedEvent(this.uuId, val, actor),
                val -> this.manifest = val
        );
    }

    public void updatePhysicalSpecs(ProductPhysicalSpecs newSpecs, Actor actor) {
        this.applyDomainChange(actor, newSpecs,
                (next, auth) -> {
                    ProductBehavior.verifyManifestUpdateAuthority(auth);
                    ProductBehavior.validatePhysicalSpecs(next);
                    return next;
                },
                val -> new ProductPhysicalSpecsUpdatedEvent(this.uuId, val, actor),
                val -> {
                    this.physicalSpecs = val;
                    ProductBehavior.validateComposition(this);
                }
        );
    }

    // --- STRUCTURAL REASSIGNMENTS ---

    public void reassignTypeList(TypeListUuId newTypeListId, Actor actor) {
        this.applyDomainChange(actor, newTypeListId,
                (next, auth) -> {
                    ProductBehavior.verifyStructuralChangeAuthority(auth);
                    return next;
                },
                val -> new ProductTypeConfigurationChangedEvent(this.uuId, val, actor),
                val -> {
                    this.typeListUuId = val;
                    ProductBehavior.validateComposition(this);
                }
        );
    }

    public void reassignPriceList(PriceListUuId newPriceListId, Actor actor) {
        this.applyDomainChange(actor, newPriceListId,
                (next, auth) -> {
                    ProductBehavior.verifyStructuralChangeAuthority(auth);
                    return next;
                },
                val -> new ProductPriceListReassignedEvent(this.uuId, val, actor),
                val -> {
                    this.priceListUuId = val;
                    ProductBehavior.validateComposition(this);
                }
        );
    }

    public void reassignGallery(GalleryUuId newGalleryId, Actor actor) {
        this.applyDomainChange(actor, newGalleryId,
                (next, auth) -> {
                    ProductBehavior.verifyManifestUpdateAuthority(auth);
                    return next;
                },
                val -> new ProductGalleryReassignedEvent(this.uuId, val, actor),
                val -> this.galleryUuId = val
        );
    }

    public void reassignVariantList(VariantListUuId newVariantListId, Actor actor) {
        this.applyDomainChange(actor, newVariantListId,
                (next, auth) -> {
                    ProductBehavior.verifyStructuralChangeAuthority(auth);
                    return next;
                },
                val -> new ProductVariantListReassignedEvent(this.uuId, val, actor),
                val -> {
                    this.variantListUuId = val;
                    ProductBehavior.validateComposition(this);
                }
        );
    }

    public void syncToKafka(Actor actor) {
        this.executeSync(actor,
                auth -> new ProductDataSyncedEvent(this.uuId, this.businessUuId,
                        this.productStatus, this.lifecycleState, auth)
        );
    }

    // --- LIFECYCLE (Standardized) ---

    public void archive(Actor actor) { this.executeArchive(actor, new ProductArchivedEvent(this.uuId, actor)); }
    public void unarchive(Actor actor) { this.executeUnarchive(actor, new ProductUnarchivedEvent(this.uuId, actor)); }
    public void softDelete(Actor actor) { this.executeSoftDelete(actor, new ProductSoftDeletedEvent(this.uuId, actor)); }
    public void restore(Actor actor) { this.executeRestore(actor, new ProductRestoredEvent(this.uuId, actor)); }
    public void hardDelete(Actor actor) { this.executeHardDelete(actor, new ProductHardDeletedEvent(this.uuId, actor)); }

    // --- GETTERS ---
    public ProductManifest getManifest() { return manifest; }
    public ProductStatus getProductStatus() { return productStatus; }
    public ProductRegion getProductRegion() {return productRegion; }
    public ProductVersion getProductVersion() { return productVersion; }
    public TypeListUuId getTypeListUuId() { return typeListUuId; }
    public PriceListUuId getPriceListUuId() { return priceListUuId; }
    public GalleryUuId getGalleryUuId() { return galleryUuId; }
    public VariantListUuId getVariantListUuId() { return variantListUuId; }
    public ProductPhysicalSpecs getPhysicalSpecs() { return physicalSpecs; }
    public ProductThumbnailUrl getProductThumbnailUrl() { return productThumbnailUrl; }
}

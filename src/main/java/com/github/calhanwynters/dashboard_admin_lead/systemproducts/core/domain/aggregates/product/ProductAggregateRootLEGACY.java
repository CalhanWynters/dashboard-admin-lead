package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.StatusEnums;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.LEGACYBaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleansLEGACY;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.events.*;

public class ProductAggregateRootLEGACY extends LEGACYBaseAggregateRoot<ProductAggregateRootLEGACY> {

    private final ProductId productId;
    private final ProductUuId productUuId;
    private ProductBusinessUuId productBusinessUuId;

    private ProductVersion productVersion;
    private ProductStatus productStatus;

    private ProductManifest manifest; // Composite class of Name, Category, and Description
    private ProductPhysicalSpecs physicalSpecs;
    private ProductBooleansLEGACY productBooleansLEGACY; // Record integration
    // Add Version-Based Optimistic Locking "optLockVer"
    // Add Schema-Based Versioning "schemaVer"

    private ProductThumbnailUrl productThumbnailUrl;

    private GalleryUuId galleryUuId;
    private VariantListUuId variantListUuId;
    private TypeListUuId typeListUuId;
    private PriceListUuId priceListUuId;

    public ProductAggregateRootLEGACY(ProductId productId,
                                      ProductUuId productUuId,
                                      ProductBusinessUuId productBusinessUuId,
                                      ProductManifest manifest,
                                      ProductVersion productVersion,
                                      ProductStatus productStatus,
                                      ProductPhysicalSpecs physicalSpecs,
                                      ProductBooleansLEGACY productBooleansLEGACY,
                                      ProductThumbnailUrl productThumbnailUrl,
                                      GalleryUuId galleryUuId,
                                      VariantListUuId variantListUuId,
                                      TypeListUuId typeListUuId,
                                      PriceListUuId priceListUuId,
                                      AuditMetadata auditMetadata) {

        super(auditMetadata);
        this.productId = productId;
        this.productUuId = DomainGuard.notNull(productUuId, "Product UUID");
        this.productBusinessUuId = DomainGuard.notNull(productBusinessUuId, "Business UUID");
        this.productVersion = DomainGuard.notNull(productVersion, "Version");
        this.productStatus = DomainGuard.notNull(productStatus, "Status");
        this.manifest = DomainGuard.notNull(manifest, "Product Manifest");
        this.productThumbnailUrl = DomainGuard.notNull(productThumbnailUrl, "Product Thumbnail");

        // Record Null-Safety
        this.productBooleansLEGACY = (productBooleansLEGACY != null) ? productBooleansLEGACY : new ProductBooleansLEGACY(false, false);

        this.typeListUuId = (typeListUuId != null) ? typeListUuId : TypeListUuId.NONE;
        this.physicalSpecs = (physicalSpecs != null) ? physicalSpecs : ProductPhysicalSpecs.NONE;
        this.priceListUuId = (priceListUuId != null) ? priceListUuId : PriceListUuId.NONE;
        this.galleryUuId = (galleryUuId != null) ? galleryUuId : GalleryUuId.NONE;
        this.variantListUuId = (variantListUuId != null) ? variantListUuId : VariantListUuId.NONE;

        ProductBehavior.validateComposition(this);
    }

    public static ProductAggregateRootLEGACY create(
            ProductUuId uuId,
            ProductBusinessUuId bUuId,
            ProductManifest manifest,
            ProductThumbnailUrl thumbnail,
            ProductStatus status,
            GalleryUuId gallery,
            VariantListUuId variants,
            TypeListUuId types,
            PriceListUuId prices,
            ProductPhysicalSpecs specs,
            Actor actor) {

        ProductBehavior.verifyCreationAuthority(actor);

        ProductAggregateRootLEGACY product = new ProductAggregateRootLEGACY(
                null,
                uuId,
                bUuId,
                manifest,
                ProductVersion.INITIAL,
                status,
                specs,
                new ProductBooleansLEGACY(false, false),
                thumbnail,
                gallery,
                variants,
                types,
                prices,
                AuditMetadata.create(actor)
        );

        product.registerEvent(new ProductCreatedEvent(uuId, bUuId, status, actor));
        return product;
    }

    // --- DOMAIN ACTIONS ---

    public void reassignVariantList(VariantListUuId newVariantListId, Actor actor) {
        // Line 1: Auth & Logic
        ProductBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        ProductBehavior.verifyStructuralChangeAuthority(actor);
        var validatedId = DomainGuard.notNull(newVariantListId, "Variant List ID");

        // Line 2: Side-Effect
        this.applyChange(actor,
                new ProductVariantListReassignedEvent(this.productUuId, validatedId, actor),
                () -> {
                    this.variantListUuId = validatedId;
                    ProductBehavior.validateComposition(this);
                }
        );
    }

    public void updateBusinessUuId(ProductBusinessUuId newId, Actor actor) {
        ProductBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());

        // Validate using your existing logic (Admin-only, non-null, difference check)
        var validatedId = ProductBehavior.evaluateBusinessIdChange(this.productBusinessUuId, newId, actor);

        this.applyChange(actor,
                new ProductBusinessUuIdChangedEvent(productUuId, this.productBusinessUuId, validatedId, actor),
                () -> this.productBusinessUuId = validatedId);
    }

    public void updateStatus(StatusEnums newStatus, Actor actor) {
        ProductBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());

        // Map raw enum to Domain Wrapper and validate logic/auth
        var targetStatus = ProductStatus.of(newStatus);
        var validatedStatus = ProductBehavior.evaluateStatusTransition(this.productStatus, targetStatus, actor);

        this.applyChange(actor,
                new ProductStatusUpdatedEvent(this.productUuId, this.productStatus, validatedStatus, actor),
                () -> this.productStatus = validatedStatus
        );
    }

    public void updateUrl(ProductThumbnailUrl newUrl, Actor actor) {
        ProductBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        var validatedUrl = ProductBehavior.evaluateUrlUpdate(this.productThumbnailUrl, newUrl, actor);

        this.applyChange(actor,
                new ProductThumbnailUrlUpdatedEvent(this.productUuId, validatedUrl, actor),
                () -> this.productThumbnailUrl = validatedUrl);
    }

    public void incrementVersion(Actor actor) {
        // Line 1: Auth & Logic
        ProductBehavior.verifyVersionUpdateAuthority(actor);
        var nextVersion = ProductBehavior.incrementVersion(this.productVersion);

        // Line 2: Side-Effect (Passes productId, nextVersion, and actor)
        this.applyChange(actor,
                new ProductVersionIncrementedEvent(this.productUuId, nextVersion, actor),
                () -> this.productVersion = nextVersion
        );
    }

    public void syncToKafka(Actor actor) {
        ProductBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        ProductBehavior.verifySyncAuthority(actor);

        this.applyChange(actor, new ProductDataSyncedEvent(productUuId, productBusinessUuId, productStatus, productBooleansLEGACY, actor), null);
    }

    public void reassignTypeList(TypeListUuId newTypeListId, Actor actor) {
        // Line 1: Auth & Logic
        ProductBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        ProductBehavior.verifyStructuralChangeAuthority(actor);
        DomainGuard.notNull(newTypeListId, "Type List ID");

        // Line 2: Side-Effect
        this.applyChange(actor,
                new ProductTypeConfigurationChangedEvent(this.productUuId, newTypeListId, actor),
                () -> {
                    this.typeListUuId = newTypeListId;
                    ProductBehavior.validateComposition(this);
                }
        );
    }

    public void updateManifest(ProductManifest newManifest, Actor actor) {
        // Line 1: Auth & Logic
        ProductBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        ProductBehavior.verifyManifestUpdateAuthority(actor);
        ProductBehavior.validateManifest(newManifest);

        // Line 2: Side-Effect
        this.applyChange(actor,
                new ProductManifestUpdatedEvent(this.productUuId, newManifest, actor),
                () -> this.manifest = newManifest
        );
    }

    public void reassignGallery(GalleryUuId newGalleryId, Actor actor) {
        // Line 1: Auth & Invariants
        ProductBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        ProductBehavior.verifyManifestUpdateAuthority(actor);
        DomainGuard.notNull(newGalleryId, "Gallery ID");

        // Line 2: Side-Effect
        this.applyChange(actor,
                new ProductGalleryReassignedEvent(this.productUuId, newGalleryId, actor),
                () -> this.galleryUuId = newGalleryId
        );
    }

    public void reassignPriceList(PriceListUuId newPriceListId, Actor actor) {
        // Line 1: Structural Auth
        ProductBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        ProductBehavior.verifyStructuralChangeAuthority(actor);
        DomainGuard.notNull(newPriceListId, "Price List ID");

        // Line 2: Side-Effect
        this.applyChange(actor,
                new ProductPriceListReassignedEvent(this.productUuId, newPriceListId, actor),
                () -> {
                    this.priceListUuId = newPriceListId;
                    ProductBehavior.validateComposition(this);
                }
        );
    }

    public void updatePhysicalSpecs(ProductPhysicalSpecs newSpecs, Actor actor) {
        // Line 1: Auth & Invariants
        ProductBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        ProductBehavior.verifyManifestUpdateAuthority(actor);
        ProductBehavior.validatePhysicalSpecs(newSpecs);

        // Line 2: Side-Effect
        this.applyChange(actor,
                new ProductPhysicalSpecsUpdatedEvent(this.productUuId, newSpecs, actor),
                () -> {
                    this.physicalSpecs = newSpecs;
                    ProductBehavior.validateComposition(this);
                }
        );
    }

    public void recordMissingDependency(String dependencyType, UuId missingId, Actor actor) {
        // Line 1: Auth (Allows System Actor)
        ProductBehavior.ensureDependencyResolution(dependencyType, false, actor);

        // Line 2: Side-Effect
        this.applyChange(actor,
                new ProductDependencyMissingEvent(this.productUuId, dependencyType, missingId, actor),
                () -> this.productStatus = ProductStatus.DRAFT
        );
    }

    public void archive(Actor actor) {
        // Line 1: Auth
        ProductBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record instance)
        this.applyChange(actor,
                new ProductArchivedEvent(this.productUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(true, this.productBooleansLEGACY.softDeleted())
        );
    }

    public void unarchive(Actor actor) {
        // Line 1: Auth
        ProductBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record instance)
        this.applyChange(actor,
                new ProductUnarchivedEvent(this.productUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(false, this.productBooleansLEGACY.softDeleted())
        );
    }

    public void softDelete(Actor actor) {
        // Line 1: Logic & Auth
        ProductBehavior.ensureActive(this.productBooleansLEGACY.softDeleted());
        ProductBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record instance)
        this.applyChange(actor,
                new ProductSoftDeletedEvent(this.productUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(this.productBooleansLEGACY.archived(), true)
        );
    }

    public void restore(Actor actor) {
        // Line 1: Logic & Auth
        if (!this.productBooleansLEGACY.softDeleted()) return;
        ProductBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record instance)
        this.applyChange(actor,
                new ProductRestoredEvent(this.productUuId, actor),
                () -> this.productBooleansLEGACY = new ProductBooleansLEGACY(this.productBooleansLEGACY.archived(), false)
        );
    }

    public void hardDelete(Actor actor) {
        // Line 1: Admin Auth
        ProductBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect
        this.applyChange(actor, new ProductHardDeletedEvent(this.productUuId, actor), null);
    }


    // --- ACCESSORS ---
    public ProductId getProductId() { return productId; }
    public ProductUuId getProductUuId() { return productUuId; }
    public ProductBusinessUuId getProductBusinessUuId() { return productBusinessUuId; }
    public ProductStatus getProductStatus() { return productStatus; }
    public ProductVersion getProductVersion() { return productVersion; }
    public ProductPhysicalSpecs getProductPhysicalSpecs() { return physicalSpecs; }
    public ProductManifest getManifest() { return manifest; }
    public ProductThumbnailUrl getProductThumbnailUrl() {return productThumbnailUrl; }
    public GalleryUuId getGalleryUuId() { return galleryUuId; }
    public VariantListUuId getVariantListUuId() { return variantListUuId; }
    public TypeListUuId getTypeListUuId() { return typeListUuId; }
    public PriceListUuId getPriceListUuId() { return priceListUuId; }
}

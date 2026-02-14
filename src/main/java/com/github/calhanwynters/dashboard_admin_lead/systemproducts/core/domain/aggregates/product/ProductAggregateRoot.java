package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;
import com.github.calhanwynters.dashboard_admin_lead.common.exceptions.DomainAuthorizationException;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.events.*;

public class ProductAggregateRoot extends BaseAggregateRoot<ProductAggregateRoot> {

    private final ProductId productId;
    private final ProductUuId productUuId;

    private ProductVersion productVersion;
    private ProductStatus productStatus;

    private ProductManifest manifest;
    private ProductPhysicalSpecs physicalSpecs;
    private ProductBooleans productBooleans; // Record integration

    private GalleryUuId galleryUuId;
    private final VariantListUuId variantListUuId;
    private TypeListUuId typeListUuId;
    private PriceListUuId priceListUuId;

    public ProductAggregateRoot(ProductId productId,
                                ProductUuId productUuId,
                                ProductBusinessUuId productBusinessUuId,
                                ProductManifest manifest,
                                ProductVersion productVersion,
                                ProductStatus productStatus,
                                ProductPhysicalSpecs physicalSpecs,
                                ProductBooleans productBooleans, // Added param
                                GalleryUuId galleryUuId,
                                VariantListUuId variantListUuId,
                                TypeListUuId typeListUuId,
                                PriceListUuId priceListUuId,
                                AuditMetadata auditMetadata) {

        super(auditMetadata);
        this.productId = productId;
        this.productUuId = DomainGuard.notNull(productUuId, "Product UUID");
        // Added missing field
        ProductBusinessUuId productBusinessUuId1 = DomainGuard.notNull(productBusinessUuId, "Business UUID");
        this.productVersion = DomainGuard.notNull(productVersion, "Version");
        this.productStatus = DomainGuard.notNull(productStatus, "Status");
        this.manifest = DomainGuard.notNull(manifest, "Product Manifest");

        // Record Null-Safety
        this.productBooleans = (productBooleans != null) ? productBooleans : new ProductBooleans(false, false);

        this.typeListUuId = (typeListUuId != null) ? typeListUuId : TypeListUuId.NONE;
        this.physicalSpecs = (physicalSpecs != null) ? physicalSpecs : ProductPhysicalSpecs.NONE;
        this.priceListUuId = (priceListUuId != null) ? priceListUuId : PriceListUuId.NONE;
        this.galleryUuId = (galleryUuId != null) ? galleryUuId : GalleryUuId.NONE;
        this.variantListUuId = (variantListUuId != null) ? variantListUuId : VariantListUuId.NONE;

        ProductBehavior.validateComposition(this);
    }

    public static ProductAggregateRoot create(ProductUuId uuId, ProductBusinessUuId bUuId, ProductManifest manifest,
                                              ProductStatus status, GalleryUuId gallery, VariantListUuId variants,
                                              TypeListUuId types, PriceListUuId prices, ProductPhysicalSpecs specs, Actor actor) {
        ProductBehavior.verifyCreationAuthority(actor);

        ProductAggregateRoot product = new ProductAggregateRoot(
                null, uuId, bUuId, manifest, ProductVersion.INITIAL, status,
                specs, new ProductBooleans(false, false), gallery, variants, types, prices, AuditMetadata.create(actor)
        );

        product.registerEvent(new ProductCreatedEvent(uuId, bUuId, status, actor));
        return product;
    }

    // --- DOMAIN ACTIONS ---

    // Need a 2-liner pattern method for ProductReassignVariantListCommand
    // Need a 2-liner pattern method for ProductEditBusUuIdCommand
    // Need a 2-liner pattern method for ProductUpdateStatusCommand
    // Fix various methods here. This file wrongfully uses version as optimistic lock.

    public void reassignTypeList(TypeListUuId newTypeListId, Actor actor) {
        // Line 1: Auth & Logic
        ProductBehavior.ensureActive(this.productBooleans.softDeleted());
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
        ProductBehavior.ensureActive(this.productBooleans.softDeleted());
        ProductBehavior.verifyManifestUpdateAuthority(actor);
        ProductBehavior.validateManifest(newManifest);

        // Line 2: Side-Effect
        this.applyChange(actor,
                new ProductManifestUpdatedEvent(this.productUuId, newManifest, actor),
                () -> {
                    this.manifest = newManifest;
                    this.productVersion = ProductBehavior.incrementVersion(this.productVersion);
                }
        );
    }

    public void reassignGallery(GalleryUuId newGalleryId, Actor actor) {
        // Line 1: Auth & Invariants
        ProductBehavior.ensureActive(this.productBooleans.softDeleted());
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
        ProductBehavior.ensureActive(this.productBooleans.softDeleted());
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
        ProductBehavior.ensureActive(this.productBooleans.softDeleted());
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

    private void applyTransition(ProductStatus nextStatus, Actor actor) {
        // Line 1: Auth & Logic
        ProductBehavior.verifyStatusTransitionAuthority(actor, nextStatus);
        ProductBehavior.validateStatusTransition(this.productStatus, nextStatus);

        var nextVersion = ProductBehavior.incrementVersion(this.productVersion);
        var oldStatus = this.productStatus;

        // Line 2: Side-Effect
        this.applyChange(actor,
                new ProductStatusChangedEvent(this.productUuId, oldStatus, nextStatus, nextVersion, actor),
                () -> {
                    this.productStatus = nextStatus;
                    this.productVersion = nextVersion;
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

    public void discontinue(Actor actor) {
        // Line 1: Auth & Logic
        ProductBehavior.verifyStatusTransitionAuthority(actor, ProductStatus.DISCONTINUED);
        ProductBehavior.validateStatusTransition(this.productStatus, ProductStatus.DISCONTINUED);

        // Line 2: Side-Effect
        this.applyChange(actor,
                new ProductDiscontinuedEvent(this.productUuId, actor),
                () -> applyTransition(ProductStatus.DISCONTINUED, actor)
        );
    }

    public void activate(Actor actor) {
        ProductBehavior.ensureActive(this.productBooleans.softDeleted());
        applyTransition(ProductStatus.ACTIVE, actor);
    }

    public void deactivate(Actor actor) {
        ProductBehavior.ensureActive(this.productBooleans.softDeleted());
        applyTransition(ProductStatus.INACTIVE, actor);
    }

    public void archive(Actor actor) {
        // Line 1: Auth
        ProductBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record instance)
        this.applyChange(actor,
                new ProductArchivedEvent(this.productUuId, actor),
                () -> this.productBooleans = new ProductBooleans(true, this.productBooleans.softDeleted())
        );
    }

    public void unarchive(Actor actor) {
        // Line 1: Auth
        ProductBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record instance)
        this.applyChange(actor,
                new ProductUnarchivedEvent(this.productUuId, actor),
                () -> this.productBooleans = new ProductBooleans(false, this.productBooleans.softDeleted())
        );
    }

    public void softDelete(Actor actor) {
        // Line 1: Logic & Auth
        ProductBehavior.ensureActive(this.productBooleans.softDeleted());
        ProductBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record instance)
        this.applyChange(actor,
                new ProductSoftDeletedEvent(this.productUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), true)
        );
    }

    public void restore(Actor actor) {
        // Line 1: Logic & Auth
        if (!this.productBooleans.softDeleted()) return;
        ProductBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect (Replace record instance)
        this.applyChange(actor,
                new ProductRestoredEvent(this.productUuId, actor),
                () -> this.productBooleans = new ProductBooleans(this.productBooleans.archived(), false)
        );
    }

    public void hardDelete(Actor actor) {
        // Line 1: Admin Auth
        ProductBehavior.verifyLifecycleAuthority(actor);

        // Line 2: Side-Effect
        this.applyChange(actor, new ProductHardDeletedEvent(this.productUuId, actor), null);
    }

    /**
     * Invariant check to ensure no actions are performed on a soft-deleted product.
     */
    public static void ensureActive(boolean isSoftDeleted) {
        if (isSoftDeleted) {
            throw new IllegalStateException("Operation failed: The product is soft-deleted.");
        }
    }

    /**
     * SOC 2 compliant authority check for high-stakes lifecycle changes
     * (delete, restore, archive).
     */
    public static void verifyLifecycleAuthority(Actor actor) {
        // Example: Only Admins or Managers can handle lifecycle events
        if (!actor.hasRole(Actor.ROLE_ADMIN) && !actor.hasRole(Actor.ROLE_MANAGER)) {
            throw new DomainAuthorizationException(
                    "Unauthorized: Actor lacks lifecycle management permissions.",
                    "AUTH-004",
                    actor
            );
        }
    }

    // --- ACCESSORS ---
    public ProductId getProductId() { return productId; }
    public ProductUuId getProductUuId() { return productUuId; }
    public ProductStatus getProductStatus() { return productStatus; }
    public ProductVersion getProductVersion() { return productVersion; }
    public ProductPhysicalSpecs getProductPhysicalSpecs() { return physicalSpecs; }
    public ProductManifest getManifest() { return manifest; }
    public GalleryUuId getGalleryUuId() { return galleryUuId; }
    public VariantListUuId getVariantListUuId() { return variantListUuId; }
    public TypeListUuId getTypeListUuId() { return typeListUuId; }
    public PriceListUuId getPriceListUuId() { return priceListUuId; }
}

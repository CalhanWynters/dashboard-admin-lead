package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.GalleryUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.VariantListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.TypeListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.PriceListUuId;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.abstractclasses.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

public class ProductAggregateRoot extends BaseAggregateRoot<ProductAggregateRoot> {

    private final ProductId productId;
    private final ProductUuId productUuId;

    private ProductVersion productVersion;
    private ProductStatus productStatus;

    private ProductManifest manifest;
    private ProductPhysicalSpecs physicalSpecs;

    private GalleryUuId galleryUuId;
    private VariantListUuId variantListUuId;
    private TypeListUuId typeListUuId;
    private PriceListUuId priceListUuId;

    public ProductAggregateRoot(ProductId productId,
                                ProductUuId productUuId,
                                ProductBusinessUuId productBusinessUuId,
                                ProductManifest manifest,
                                ProductVersion productVersion,
                                ProductStatus productStatus,
                                ProductPhysicalSpecs physicalSpecs,
                                GalleryUuId galleryUuId,
                                VariantListUuId variantListUuId,
                                TypeListUuId typeListUuId,
                                PriceListUuId priceListUuId,
                                AuditMetadata auditMetadata) {

        super(auditMetadata);
        this.productId = productId;
        this.productUuId = DomainGuard.notNull(productUuId, "Product UUID");
        ProductBusinessUuId productBusinessUuId1 = DomainGuard.notNull(productBusinessUuId, "Business UUID");
        this.productVersion = DomainGuard.notNull(productVersion, "Version");
        this.productStatus = DomainGuard.notNull(productStatus, "Status");
        this.manifest = DomainGuard.notNull(manifest, "Product Manifest");

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

        ProductAggregateRoot product = new ProductAggregateRoot(
                null, uuId, bUuId, manifest, ProductVersion.INITIAL, status,
                specs, gallery, variants, types, prices, AuditMetadata.create(actor)
        );

        product.registerEvent(new ProductCreatedEvent(uuId, bUuId, status, actor));
        return product;
    }

    // --- DOMAIN ACTIONS ---

    public void updateManifest(ProductManifest newManifest, Actor actor) {
        ProductBehavior.validateManifest(newManifest);
        this.applyChange(actor,
                new ProductManifestUpdatedEvent(this.productUuId, newManifest, actor),
                () -> {
                    this.manifest = newManifest;
                    this.productVersion = ProductBehavior.incrementVersion(this.productVersion);
                }
        );
    }

    public void reassignGallery(GalleryUuId newGalleryId, Actor actor) {
        DomainGuard.notNull(newGalleryId, "Gallery ID");
        this.applyChange(actor,
                new ProductGalleryReassignedEvent(this.productUuId, newGalleryId, actor),
                () -> this.galleryUuId = newGalleryId
        );
    }

    public void reassignPriceList(PriceListUuId newPriceListId, Actor actor) {
        DomainGuard.notNull(newPriceListId, "Price List ID");
        this.applyChange(actor,
                new ProductPriceListReassignedEvent(this.productUuId, newPriceListId, actor),
                () -> {
                    this.priceListUuId = newPriceListId;
                    ProductBehavior.validateComposition(this);
                }
        );
    }

    public void updatePhysicalSpecs(ProductPhysicalSpecs newSpecs, Actor actor) {
        ProductBehavior.validatePhysicalSpecs(newSpecs);
        this.applyChange(actor,
                new ProductPhysicalSpecsUpdatedEvent(this.productUuId, newSpecs, actor),
                () -> {
                    this.physicalSpecs = newSpecs;
                    ProductBehavior.validateComposition(this);
                }
        );
    }

    private void applyTransition(ProductStatus nextStatus, Actor actor) {
        ProductBehavior.validateStatusTransition(this.productStatus, nextStatus);
        var nextVersion = ProductBehavior.incrementVersion(this.productVersion);
        var oldStatus = this.productStatus;

        this.applyChange(actor,
                new ProductStatusChangedEvent(this.productUuId, oldStatus, nextStatus, nextVersion, actor),
                () -> {
                    this.productStatus = nextStatus;
                    this.productVersion = nextVersion;
                }
        );
    }

    public void activate(Actor actor) { applyTransition(ProductStatus.ACTIVE, actor); }
    public void deactivate(Actor actor) { applyTransition(ProductStatus.INACTIVE, actor); }
    public void discontinue(Actor actor) { applyTransition(ProductStatus.DISCONTINUED, actor); }
    public void softDelete(Actor actor) { this.applyChange(actor, new ProductSoftDeletedEvent(this.productUuId, actor), null); }
    public void hardDelete(Actor actor) { this.applyChange(actor, new ProductHardDeletedEvent(this.productUuId, actor), null); }

    // --- ACCESSORS ---
    public ProductId getProductId() { return productId; }
    public ProductUuId getProductUuId() { return productUuId; }
    public ProductStatus getProductStatus() { return productStatus; }
    public ProductVersion getProductVersion() { return productVersion; }
    public ProductPhysicalSpecs getProductPhysicalSpecs() { return physicalSpecs; }
    public GalleryUuId getGalleryUuId() { return galleryUuId; }
    public VariantListUuId getVariantListUuId() { return variantListUuId; }
    public TypeListUuId getTypeListUuId() { return typeListUuId; }
    public PriceListUuId getPriceListUuId() { return priceListUuId; }
}

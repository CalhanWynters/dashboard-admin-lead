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
    private final ProductBusinessUuId productBusinessUuId;

    private ProductVersion productVersion;
    private ProductStatus productStatus;

    private final ProductManifest manifest;
    private final ProductPhysicalSpecs physicalSpecs;

    private final GalleryUuId galleryUuId;
    private final VariantListUuId variantListUuId;
    private final TypeListUuId typeListUuId;
    private final PriceListUuId priceListUuId;

    protected ProductAggregateRoot(ProductId productId,
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

        // 1. Mandatory Identity & Metadata
        DomainGuard.notNull(productId, "Product ID");
        DomainGuard.notNull(productUuId, "Product UUID");
        DomainGuard.notNull(productBusinessUuId, "Business UUID");
        DomainGuard.notNull(productVersion, "Version");
        DomainGuard.notNull(productStatus, "Status");
        DomainGuard.notNull(manifest, "Product Manifest");

        this.productId = productId;
        this.productUuId = productUuId;
        this.productBusinessUuId = productBusinessUuId;
        this.productVersion = productVersion;
        this.productStatus = productStatus;
        this.manifest = manifest;

        // 2. Optional/XOR Fields (Null-to-NONE safety)
        this.typeListUuId = (typeListUuId != null) ? typeListUuId : TypeListUuId.NONE;
        this.physicalSpecs = (physicalSpecs != null) ? physicalSpecs : ProductPhysicalSpecs.NONE;
        this.priceListUuId = (priceListUuId != null) ? priceListUuId : PriceListUuId.NONE;
        this.galleryUuId = (galleryUuId != null) ? galleryUuId : GalleryUuId.NONE;
        this.variantListUuId = (variantListUuId != null) ? variantListUuId : VariantListUuId.NONE;

        // 3. Business Invariant Validation
        DomainGuard.ensure(
                new ProductCompositionSpecification().isSatisfiedBy(this),
                "Invalid Product Type: Standard products (with TypeList) cannot have local Price/Specs. " +
                        "Bespoke products (without TypeList) require both Price and Specs.",
                "VAL-016", "INVARIANT_VIOLATION"
        );
    }

    // --- BEHAVIORAL METHODS

    public void activate(Actor actor) {
        applyTransition(ProductStatus.ACTIVE, actor);
    }

    public void deactivate(Actor actor) {
        applyTransition(ProductStatus.INACTIVE, actor);
    }

    public void discontinue(Actor actor) {
        applyTransition(ProductStatus.DISCONTINUED, actor);
    }

    private void applyTransition(ProductStatus nextStatus, Actor actor) {
        DomainGuard.ensure(
                this.productStatus.canTransitionTo(nextStatus),
                "Illegal transition from %s to %s.".formatted(this.productStatus, nextStatus),
                "VAL-016", "STATE_VIOLATION"
        );

        this.productStatus = nextStatus;
        this.productVersion = new ProductVersion(this.productVersion.value().next());
        this.recordUpdate(actor);
    }

    // Getters
    public ProductId getProductId() { return productId; }
    public ProductUuId getProductUuId() { return productUuId; }
    public ProductBusinessUuId getProductBusinessUuId() { return productBusinessUuId; }
    public ProductName getProductName() { return manifest.name(); }
    public ProductCategory getProductCategory() { return manifest.category(); }
    public ProductVersion getProductVersion() { return productVersion; }
    public ProductDescription getProductDescription() { return manifest.description(); }
    public ProductStatus getProductStatus() { return productStatus; }
    public ProductPhysicalSpecs getProductPhysicalSpecs() { return physicalSpecs; }

    public ProductWeight getProductWeight() {
        return new ProductWeight(physicalSpecs.value().weight());
    }
    public ProductDimensions getProductDimensions() {
        return new ProductDimensions(physicalSpecs.value().dimensions());
    }
    public ProductCareInstructions getProductCareInstructions() {
        return new ProductCareInstructions(physicalSpecs.value().careInstructions());
    }

    public GalleryUuId getGalleryUuId() { return galleryUuId; }
    public VariantListUuId getVariantListUuId() { return variantListUuId; }
    public TypeListUuId getTypeListUuId() { return typeListUuId; }
    public PriceListUuId getPriceListUuId() { return priceListUuId; }

}

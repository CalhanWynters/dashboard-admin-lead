package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.GalleryUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.VariantListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.TypeListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.PriceListUuId;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.BaseAggregateRoot;
import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;

/**
 * Pure Domain Aggregate - No JPA/Infrastructure annotations.
 */
public class ProductAggregate extends BaseAggregateRoot<ProductAggregate> {

    private final ProductId productId;
    private final ProductUuId productUuId;
    private final ProductBusinessUuId productBusinessUuId;

    private ProductVersion productVersion;
    private ProductStatus productStatus;

    // Grouped Domain Logic
    private final ProductManifest manifest;
    private final ProductPhysicalSpecs physicalSpecs;

    // Aggregation References
    private final GalleryUuId galleryUuId;
    private final VariantListUuId variantListUuId;
    private final TypeListUuId typeListUuId;
    private final PriceListUuId priceListUuId;

    protected ProductAggregate(ProductId productId,
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

        super(auditMetadata); // Handles temporal business logic

        // 1. Validate Mandatory Identity & Metadata
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

        // 2. XOR Logic: Handle Physical Composition
        boolean hasType = typeListUuId != null && !typeListUuId.equals(TypeListUuId.NONE);

        if (hasType) {
            this.typeListUuId = typeListUuId;
            this.physicalSpecs = ProductPhysicalSpecs.NONE;
        } else {
            this.typeListUuId = TypeListUuId.NONE;
            DomainGuard.ensure(
                    physicalSpecs != null && !physicalSpecs.isNone(),
                    "Bespoke Product requires Physical Specs",
                    "VAL-015", "INVARIANT_VIOLATION"
            );
            this.physicalSpecs = physicalSpecs;
        }

        this.galleryUuId = (galleryUuId != null) ? galleryUuId : GalleryUuId.NONE;
        this.variantListUuId = (variantListUuId != null) ? variantListUuId : VariantListUuId.NONE;
        this.priceListUuId = (priceListUuId != null) ? priceListUuId : PriceListUuId.NONE;
    }

    public void updateStatus(ProductStatus newStatus, Actor actor) {
        this.productStatus = newStatus;
        this.recordUpdate(actor);
    }

    public void incrementVersion(Actor actor) {
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

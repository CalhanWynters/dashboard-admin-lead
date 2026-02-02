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
    private final ProductName productName;
    private final ProductCategory productCategory;
    private final ProductDescription productDescription;

    private ProductVersion productVersion;
    private ProductStatus productStatus;

    // Composition traits
    private final ProductWeight productWeight;
    private final ProductDimensions productDimensions;
    private final ProductCareInstructions productCareInstructions;

    // Aggregation References
    private final GalleryUuId galleryUuId;
    private final VariantListUuId variantListUuId;
    private final TypeListUuId typeListUuId;
    private final PriceListUuId priceListUuId;

    protected ProductAggregate(ProductId productId,
                               ProductUuId productUuId,
                               ProductBusinessUuId productBusinessUuId,
                               ProductName productName,
                               ProductCategory productCategory,
                               ProductVersion productVersion,
                               ProductDescription productDescription,
                               ProductStatus productStatus,
                               ProductWeight productWeight,
                               ProductDimensions productDimensions,
                               ProductCareInstructions productCareInstructions,
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
        DomainGuard.notNull(productName, "Product Name");
        DomainGuard.notNull(productCategory, "Category");
        DomainGuard.notNull(productVersion, "Version");
        DomainGuard.notNull(productDescription, "Description");
        DomainGuard.notNull(productStatus, "Status");

        this.productId = productId;
        this.productUuId = productUuId;
        this.productBusinessUuId = productBusinessUuId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productVersion = productVersion;
        this.productDescription = productDescription;
        this.productStatus = productStatus;

        // 2. XOR Logic: Handle Conditional Composition
        if (typeListUuId != null && !typeListUuId.equals(TypeListUuId.NONE)) {
            this.typeListUuId = typeListUuId;
            this.productWeight = ProductWeight.NONE;
            this.productDimensions = ProductDimensions.NONE;
            this.productCareInstructions = ProductCareInstructions.NONE;
        } else {
            this.typeListUuId = TypeListUuId.NONE;
            DomainGuard.notNull(productWeight, "Bespoke Product Weight");
            DomainGuard.notNull(productDimensions, "Bespoke Product Dimensions");
            DomainGuard.notNull(productCareInstructions, "Bespoke Product Care Instructions");

            this.productWeight = productWeight;
            this.productDimensions = productDimensions;
            this.productCareInstructions = productCareInstructions;
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

    // Getters remain purely functional...
    public ProductId getProductId() { return productId; }
    public ProductUuId getProductUuId() { return productUuId; }
    public ProductBusinessUuId getProductBusinessUuId() { return productBusinessUuId; }
    public ProductName getProductName() { return productName; }
    public ProductCategory getProductCategory() { return productCategory; }
    public ProductVersion getProductVersion() { return productVersion; }
    public ProductDescription getProductDescription() { return productDescription; }
    public ProductStatus getProductStatus() { return productStatus; }
    public ProductWeight getProductWeight() { return productWeight; }
    public ProductDimensions getProductDimensions() { return productDimensions; }
    public ProductCareInstructions getProductCareInstructions() { return productCareInstructions; }
    public GalleryUuId getGalleryUuId() { return galleryUuId; }
    public VariantListUuId getVariantListUuId() { return variantListUuId; }
    public TypeListUuId getTypeListUuId() { return typeListUuId; }
    public PriceListUuId getPriceListUuId() { return priceListUuId; }
}

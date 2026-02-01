package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.GalleryUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.VariantListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.TypeListUuId;
// Import the PriceListUuId from its domain wrapper
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.PriceListUuId;

import com.github.calhanwynters.dashboard_admin_lead.common.validationchecks.DomainGuard;
import org.springframework.data.domain.AbstractAggregateRoot;

public class ProductAggregate extends AbstractAggregateRoot<ProductAggregate> {

    private final ProductId productId;
    private final ProductUuId productUuId;
    private final ProductBusinessUuId productBusinessUuId;
    private final ProductName productName;
    private final ProductCategory productCategory;
    private final ProductVersion productVersion;
    private final ProductDescription productDescription;
    private final ProductStatus productStatus;
    private final ProductWeight productWeight;
    private final ProductDimensions productDimensions;
    private final ProductCareInstructions productCareInstructions;

    // Aggregation References
    private final GalleryUuId galleryUuId;
    private final VariantListUuId variantListUuId;
    private final TypeListUuId typeListUuId;
    private final PriceListUuId priceListUuId;

    // Should handle FeatureCompatibilityPolicy and IncompatibilityRule mechanisms in Domain Services?? or is application services?

    public ProductAggregate(ProductId productId,
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
                            PriceListUuId priceListUuId) {

        DomainGuard.notNull(productId, "ProductAggregate ID");
        DomainGuard.notNull(productUuId, "ProductAggregate UUID");
        DomainGuard.notNull(productBusinessUuId, "ProductAggregate Business UUID");
        DomainGuard.notNull(productName, "ProductAggregate Name");
        DomainGuard.notNull(productCategory, "ProductAggregate Category");
        DomainGuard.notNull(productVersion, "ProductAggregate Version");
        DomainGuard.notNull(productDescription, "ProductAggregate Description");
        DomainGuard.notNull(productStatus, "ProductAggregate Status");
        DomainGuard.notNull(productWeight, "ProductAggregate Weight");
        DomainGuard.notNull(productDimensions, "ProductAggregate Dimensions");
        DomainGuard.notNull(productCareInstructions, "ProductAggregate Care Instructions");

        // Reference Validations
        DomainGuard.notNull(galleryUuId, "Associated Gallery UUID");
        DomainGuard.notNull(variantListUuId, "Associated Variant List UUID");
        DomainGuard.notNull(typeListUuId, "Associated Type List UUID");
        DomainGuard.notNull(priceListUuId, "Associated Price List UUID");

        this.productId = productId;
        this.productUuId = productUuId;
        this.productBusinessUuId = productBusinessUuId;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productVersion = productVersion;
        this.productDescription = productDescription;
        this.productStatus = productStatus;
        this.productWeight = productWeight;
        this.productDimensions = productDimensions;
        this.productCareInstructions = productCareInstructions;

        this.galleryUuId = galleryUuId;
        this.variantListUuId = variantListUuId;
        this.typeListUuId = typeListUuId;
        this.priceListUuId = priceListUuId;
    }

    // Standard Getters
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

    // Reference Getters
    public GalleryUuId getGalleryUuId() { return galleryUuId; }
    public VariantListUuId getVariantListUuId() { return variantListUuId; }
    public TypeListUuId getTypeListUuId() { return typeListUuId; }
    public PriceListUuId getPriceListUuId() { return priceListUuId; }
}

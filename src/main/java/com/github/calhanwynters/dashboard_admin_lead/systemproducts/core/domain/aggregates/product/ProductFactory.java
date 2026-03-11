package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.PhysicalSpecs;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleans;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Factory for creating and reconstituting ProductAggregateRoot instances.
 * Ensures alignment with the 15-parameter constructor in ProductAggregateRoot.
 */
public class ProductFactory {

    private ProductFactory() {}

    /**
     * Creates a Bespoke product with complex physical specifications.
     */
    public static ProductAggregateRootLEGACY createBespoke(
            ProductBusinessUuId businessId,
            ProductName name,
            ProductCategory category,
            ProductDescription description,
            ProductThumbnailUrl thumbnail,
            ProductWeight weight,
            ProductDimensions dimensions,
            ProductCareInstructions careInstructions,
            PriceListUuId priceListId,
            VariantListUuId variantListId,
            Actor actor) {

        ProductManifest manifest = new ProductManifest(name, category, description);
        ProductPhysicalSpecs physicalSpecs = new ProductPhysicalSpecs(
                new PhysicalSpecs(weight.value(), dimensions.value(), careInstructions.value())
        );

        return new ProductAggregateRootLEGACY(
                null,                           // productId
                ProductUuId.generate(),         // productUuId
                businessId,                     // productBusinessUuId
                manifest,                       // manifest
                ProductVersion.INITIAL,         // productVersion
                ProductStatus.DRAFT,            // productStatus
                physicalSpecs,                  // physicalSpecs
                new ProductBooleans(false, false), // productBooleans
                thumbnail,                      // productThumbnailUrl
                GalleryUuId.generate(),         // galleryUuId
                (variantListId != null) ? variantListId : VariantListUuId.NONE,
                TypeListUuId.NONE,              // typeListUuId
                priceListId,                    // priceListUuId
                AuditMetadata.create(actor)     // auditMetadata
        );
    }

    /**
     * Creates a Standard product with minimal physical overhead.
     */
    public static ProductAggregateRootLEGACY createStandard(
            ProductBusinessUuId businessId,
            ProductName name,
            ProductCategory category,
            ProductDescription description,
            ProductThumbnailUrl thumbnail,
            TypeListUuId typeListId,
            VariantListUuId variantListId,
            Actor actor) {

        return new ProductAggregateRootLEGACY(
                null,                           // productId
                ProductUuId.generate(),         // productUuId
                businessId,                     // productBusinessUuId
                new ProductManifest(name, category, description),
                ProductVersion.INITIAL,         // productVersion
                ProductStatus.DRAFT,            // productStatus
                ProductPhysicalSpecs.NONE,      // physicalSpecs
                new ProductBooleans(false, false),
                thumbnail,                      // productThumbnailUrl
                GalleryUuId.generate(),         // galleryUuId
                (variantListId != null) ? variantListId : VariantListUuId.NONE,
                typeListId,                     // typeListUuId
                PriceListUuId.NONE,             // priceListUuId
                AuditMetadata.create(actor)
        );
    }

    /**
     * Reconstitutes an existing product from persistence.
     */
    public static ProductAggregateRootLEGACY reconstitute(
            ProductId id,
            ProductUuId uuId,
            ProductBusinessUuId businessId,
            ProductManifest manifest,
            ProductVersion version,
            ProductStatus status,
            ProductPhysicalSpecs physicalSpecs,
            ProductBooleans productBooleans,
            ProductThumbnailUrl thumbnail,
            GalleryUuId galleryId,
            VariantListUuId variantId,
            TypeListUuId typeId,
            PriceListUuId priceId,
            AuditMetadata auditMetadata) {

        return new ProductAggregateRootLEGACY(
                id,
                uuId,
                businessId,
                manifest,
                version,
                status,
                physicalSpecs,
                productBooleans,
                thumbnail,
                galleryId,
                variantId,
                typeId,
                priceId,
                auditMetadata
        );
    }
}

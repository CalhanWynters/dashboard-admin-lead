package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.PhysicalSpecs;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.GalleryUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.VariantListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.TypeListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.PriceListUuId;

public class ProductFactory {

    private ProductFactory() {}

    /**
     * Creates a Bespoke Product (Type 1 or 3).
     * Requires PriceList and PhysicalSpecs.
     */
    public static ProductAggregateRoot createBespoke(
            ProductBusinessUuId businessId,
            ProductName name,
            ProductCategory category,
            ProductDescription description,
            ProductWeight weight,
            ProductDimensions dimensions,
            ProductCareInstructions careInstructions,
            PriceListUuId priceListId, // REQUIRED for Type 1/3
            VariantListUuId variantListId, // Can be NONE (Type 1) or ID (Type 3)
            Actor creator) {

        ProductManifest manifest = new ProductManifest(name, category, description);
        ProductPhysicalSpecs physicalSpecs = new ProductPhysicalSpecs(
                new PhysicalSpecs(weight.value(), dimensions.value(), careInstructions.value())
        );

        return new ProductAggregateRoot(
                ProductId.of(0L),
                ProductUuId.generate(),
                businessId,
                manifest,
                ProductVersion.INITIAL,
                ProductStatus.DRAFT,
                physicalSpecs,
                GalleryUuId.generate(),
                (variantListId != null) ? variantListId : VariantListUuId.NONE,
                TypeListUuId.NONE,
                priceListId, // Correctly assigned
                AuditMetadata.create(creator)
        );
    }

    /**
     * Creates a Standard Product (Type 2 or 4).
     * Inherits attributes; PriceList and PhysicalSpecs must be NONE.
     */
    public static ProductAggregateRoot createStandard(
            ProductBusinessUuId businessId,
            ProductName name,
            ProductCategory category,
            ProductDescription description,
            TypeListUuId typeListId, // REQUIRED for Type 2/4
            VariantListUuId variantListId, // Can be NONE (Type 2) or ID (Type 4)
            Actor creator) {

        return new ProductAggregateRoot(
                ProductId.of(0L),
                ProductUuId.generate(),
                businessId,
                new ProductManifest(name, category, description),
                ProductVersion.INITIAL,
                ProductStatus.DRAFT,
                ProductPhysicalSpecs.NONE,
                GalleryUuId.generate(),
                (variantListId != null) ? variantListId : VariantListUuId.NONE,
                typeListId,
                PriceListUuId.NONE,
                AuditMetadata.create(creator)
        );
    }

    public static ProductAggregateRoot reconstitute(
            ProductId id, ProductUuId uuId, ProductBusinessUuId businessId,
            ProductManifest manifest, ProductVersion version, ProductStatus status,
            ProductPhysicalSpecs physicalSpecs, GalleryUuId galleryId,
            VariantListUuId variantId, TypeListUuId typeId, PriceListUuId priceId,
            AuditMetadata auditMetadata) {

        return new ProductAggregateRoot(id, uuId, businessId, manifest, version,
                status, physicalSpecs, galleryId, variantId,
                typeId, priceId, auditMetadata);
    }
}

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

    /**
     * Bespoke Product: Requires direct physical attributes.
     */
    public static ProductAggregate createBespoke(
            ProductBusinessUuId businessId,
            ProductName name,
            ProductCategory category,
            ProductDescription desc,
            ProductWeight weight,
            ProductDimensions dim,
            ProductCareInstructions care,
            VariantListUuId variantListId,
            Actor creator) {

        ProductManifest manifest = new ProductManifest(name, category, desc);

        // Wrap the common PhysicalSpecs into the Product domain wrapper
        ProductPhysicalSpecs physicalSpecs = new ProductPhysicalSpecs(
                new PhysicalSpecs(weight.value(), dim.value(), care.value())
        );

        return new ProductAggregate(
                ProductId.of(0L),
                ProductUuId.generate(),
                businessId,
                manifest,
                ProductVersion.INITIAL,
                ProductStatus.DRAFT,
                physicalSpecs,
                GalleryUuId.generate(),
                variantListId,
                TypeListUuId.NONE,
                PriceListUuId.NONE,
                AuditMetadata.create(creator)
        );
    }

    /**
     * Standard Product: Inherits physical attributes from a Type.
     */
    public static ProductAggregate createStandard(
            ProductBusinessUuId businessId,
            ProductName name,
            ProductCategory category,
            ProductDescription desc,
            TypeListUuId typeListId,
            VariantListUuId variantListId,
            Actor creator) {

        ProductManifest manifest = new ProductManifest(name, category, desc);

        return new ProductAggregate(
                ProductId.of(0L),
                ProductUuId.generate(),
                businessId,
                manifest,
                ProductVersion.INITIAL,
                ProductStatus.DRAFT,
                ProductPhysicalSpecs.NONE,
                GalleryUuId.generate(),
                variantListId,
                typeListId,
                PriceListUuId.NONE,
                AuditMetadata.create(creator)
        );
    }

    /**
     * Reconstitution Factory: Used by Repositories to load existing data.
     */
    public static ProductAggregate reconstitute(
            ProductId id, ProductUuId uuId, ProductBusinessUuId bizId,
            ProductManifest manifest, ProductVersion ver, ProductStatus status,
            ProductPhysicalSpecs physicalSpecs, GalleryUuId galleryId,
            VariantListUuId variantId, TypeListUuId typeId, PriceListUuId priceId,
            AuditMetadata auditMetadata) {

        return new ProductAggregate(
                id, uuId, bizId, manifest, ver, status,
                physicalSpecs, galleryId, variantId, typeId, priceId,
                auditMetadata
        );
    }
}

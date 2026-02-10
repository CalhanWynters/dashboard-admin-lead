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

public class ProductFactory {

    private ProductFactory() {}

    public static ProductAggregateRoot createBespoke(
            ProductBusinessUuId businessId, ProductName name, ProductCategory category,
            ProductDescription description, ProductWeight weight, ProductDimensions dimensions,
            ProductCareInstructions careInstructions, PriceListUuId priceListId,
            VariantListUuId variantListId, Actor actor) {

        ProductManifest manifest = new ProductManifest(name, category, description);
        ProductPhysicalSpecs physicalSpecs = new ProductPhysicalSpecs(
                new PhysicalSpecs(weight.value(), dimensions.value(), careInstructions.value())
        );

        return new ProductAggregateRoot(
                null, ProductUuId.generate(), businessId, manifest, ProductVersion.INITIAL,
                ProductStatus.DRAFT, physicalSpecs, new ProductBooleans(false, false), // Added Record
                GalleryUuId.generate(), (variantListId != null) ? variantListId : VariantListUuId.NONE,
                TypeListUuId.NONE, priceListId, AuditMetadata.create(actor)
        );
    }

    public static ProductAggregateRoot createStandard(
            ProductBusinessUuId businessId, ProductName name, ProductCategory category,
            ProductDescription description, TypeListUuId typeListId,
            VariantListUuId variantListId, Actor actor) {

        return new ProductAggregateRoot(
                null, ProductUuId.generate(), businessId, new ProductManifest(name, category, description),
                ProductVersion.INITIAL, ProductStatus.DRAFT, ProductPhysicalSpecs.NONE,
                new ProductBooleans(false, false), // Added Record
                GalleryUuId.generate(), (variantListId != null) ? variantListId : VariantListUuId.NONE,
                typeListId, PriceListUuId.NONE, AuditMetadata.create(actor)
        );
    }

    public static ProductAggregateRoot reconstitute(
            ProductId id, ProductUuId uuId, ProductBusinessUuId businessId,
            ProductManifest manifest, ProductVersion version, ProductStatus status,
            ProductPhysicalSpecs physicalSpecs, ProductBooleans productBooleans, // Added Record
            GalleryUuId galleryId, VariantListUuId variantId, TypeListUuId typeId,
            PriceListUuId priceId, AuditMetadata auditMetadata) {

        return new ProductAggregateRoot(id, uuId, businessId, manifest, version,
                status, physicalSpecs, productBooleans, galleryId, variantId,
                typeId, priceId, auditMetadata);
    }
}


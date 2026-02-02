package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.AuditMetadata;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.GalleryUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.VariantListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.TypeListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.PriceListUuId;

public class ProductFactory {

    /**
     * PERMUTATION 1 & 2: Bespoke Product
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
            Actor creator) { // Added Actor

        return new ProductAggregate(
                ProductId.of(0L),
                ProductUuId.generate(),
                businessId,
                name,
                category,
                ProductVersion.INITIAL,
                desc,
                ProductStatus.DRAFT,
                weight,
                dim,
                care,
                GalleryUuId.generate(),
                variantListId,
                TypeListUuId.NONE,
                PriceListUuId.NONE,
                AuditMetadata.create(creator) // Generate initial audit trail
        );
    }

    /**
     * PERMUTATION 3 & 4: Standard Product
     */
    public static ProductAggregate createStandard(
            ProductBusinessUuId businessId,
            ProductName name,
            ProductCategory category,
            ProductDescription desc,
            TypeListUuId typeListId,
            VariantListUuId variantListId,
            Actor creator) { // Added Actor

        return new ProductAggregate(
                ProductId.of(0L),
                ProductUuId.generate(),
                businessId,
                name,
                category,
                ProductVersion.INITIAL,
                desc,
                ProductStatus.DRAFT,
                ProductWeight.NONE,
                ProductDimensions.NONE,
                ProductCareInstructions.NONE,
                GalleryUuId.generate(),
                variantListId,
                typeListId,
                PriceListUuId.NONE,
                AuditMetadata.create(creator) // Generate initial audit trail
        );
    }

    /**
     * Reconstitution Factory
     * Passes existing AuditMetadata directly from the database/repository.
     */
    public static ProductAggregate reconstitute(
            ProductId id, ProductUuId uuId, ProductBusinessUuId bizId, ProductName name,
            ProductCategory cat, ProductVersion ver, ProductDescription desc, ProductStatus status,
            ProductWeight weight, ProductDimensions dim, ProductCareInstructions care,
            GalleryUuId galleryId, VariantListUuId variantId, TypeListUuId typeId, PriceListUuId priceId,
            AuditMetadata auditMetadata) { // Added AuditMetadata

        return new ProductAggregate(
                id, uuId, bizId, name, cat, ver, desc, status,
                weight, dim, care, galleryId, variantId, typeId, priceId,
                auditMetadata
        );
    }
}

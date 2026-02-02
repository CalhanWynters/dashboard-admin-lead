package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product;

import com.github.calhanwynters.dashboard_admin_lead.common.UuId;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.product.ProductDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.gallery.GalleryDomainWrapper.GalleryUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.variantlist.VariantListDomainWrapper.VariantListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.typelist.TypeListDomainWrapper.TypeListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Modern Factory for Product Aggregates.
 * Maps legacy permutations to the composition-based ProductAggregate.
 */
public class ProductFactory {

    /**
     * PERMUTATION 1 & 2: Bespoke Product
     * Used for unique architectures with local physical specs.
     */
    public static ProductAggregate createBespoke(
            ProductBusinessUuId businessId,
            ProductName name,
            ProductCategory category,
            ProductDescription desc,
            ProductWeight weight,
            ProductDimensions dim,
            ProductCareInstructions care,
            VariantListUuId variantListId) {

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
                PriceListUuId.NONE
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
            VariantListUuId variantListId) {

        return new ProductAggregate(
                ProductId.of(0L),
                new ProductUuId(UuId.generate()),
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
                PriceListUuId.NONE
        );
    }

    /**
     * Reconstitution Factory
     * Used by Repositories to hydrate existing data back into the Aggregate.
     */
    public static ProductAggregate reconstitute(
            ProductId id, ProductUuId uuId, ProductBusinessUuId bizId, ProductName name,
            ProductCategory cat, ProductVersion ver, ProductDescription desc, ProductStatus status,
            ProductWeight weight, ProductDimensions dim, ProductCareInstructions care,
            GalleryUuId galleryId, VariantListUuId variantId, TypeListUuId typeId, PriceListUuId priceId) {

        return new ProductAggregate(
                id, uuId, bizId, name, cat, ver, desc, status,
                weight, dim, care, galleryId, variantId, typeId, priceId
        );
    }
}

package com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product;

import com.github.calhanwynters.dashboard_admin_lead.common.Actor;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;

import java.time.OffsetDateTime;

import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;

/**
 * Refactored Product Factory (2026 Edition).
 * Orchestrates the creation of Bespoke and Standard product variants.
 */
public class ProductFactory {

    private ProductFactory() {}

    /**
     * Creates a Bespoke product (Manual Pricing & Specs required).
     */
    public static ProductAggregate createBespoke(
            ProductBusinessUuId businessId,
            ProductManifest manifest,
            ProductThumbnailUrl thumbnail,
            ProductPhysicalSpecs physicalSpecs,
            PriceListUuId priceListId,
            VariantListUuId variantListId,
            Actor actor) {

        // Validate via Behavior before instantiation
        ProductUuId newUuId = ProductUuId.generate();
        ProductBehavior.validateCreation(newUuId, businessId, actor);

        return new ProductAggregate(
                null, newUuId, businessId, manifest,
                ProductVersion.INITIAL, ProductStatus.DRAFT,
                physicalSpecs, thumbnail,
                GalleryUuId.generate(),
                (variantListId != null) ? variantListId : VariantListUuId.NONE,
                TypeListUuId.NONE,
                priceListId,
                AuditMetadata.create(actor),
                new LifecycleState(false, false),
                0L, 1, null
        );
    }

    /**
     * Creates a Standard product (Inherits Pricing & Specs from TypeList).
     */
    public static ProductAggregate createStandard(
            ProductBusinessUuId businessId,
            ProductManifest manifest,
            ProductThumbnailUrl thumbnail,
            TypeListUuId typeListId,
            VariantListUuId variantListId,
            Actor actor) {

        ProductUuId newUuId = ProductUuId.generate();
        ProductBehavior.validateCreation(newUuId, businessId, actor);

        return new ProductAggregate(
                null, newUuId, businessId, manifest,
                ProductVersion.INITIAL, ProductStatus.DRAFT,
                ProductPhysicalSpecs.NONE, thumbnail,
                GalleryUuId.generate(),
                (variantListId != null) ? variantListId : VariantListUuId.NONE,
                typeListId,
                PriceListUuId.NONE,
                AuditMetadata.create(actor),
                new LifecycleState(false, false),
                0L, 1, null
        );
    }

    /**
     * Reconstitutes an existing product from Database.
     */
    public static ProductAggregate reconstitute(
            ProductId id, ProductUuId uuId, ProductBusinessUuId businessId,
            ProductManifest manifest, ProductVersion version, ProductStatus status,
            ProductPhysicalSpecs physicalSpecs, LifecycleState lifecycleState,
            ProductThumbnailUrl thumbnail, GalleryUuId galleryId,
            VariantListUuId variantId, TypeListUuId typeId, PriceListUuId priceId,
            AuditMetadata auditMetadata, Long optLockVer, Integer schemaVer,
            OffsetDateTime lastSyncedAt) {

        return new ProductAggregate(
                id, uuId, businessId, manifest, version, status,
                physicalSpecs, thumbnail, galleryId, variantId,
                typeId, priceId, auditMetadata, lifecycleState,
                optLockVer, schemaVer, lastSyncedAt
        );
    }
}

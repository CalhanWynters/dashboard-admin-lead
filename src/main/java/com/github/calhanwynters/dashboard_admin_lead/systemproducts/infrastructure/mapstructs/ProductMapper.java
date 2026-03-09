package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.gallery.GalleryDomainWrapper.GalleryUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.variantlist.VariantListDomainWrapper.VariantListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.typelist.TypeListDomainWrapper.TypeListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.PriceListUuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.product.ProductDomainWrapper.*;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "productId", source = "id", qualifiedByName = "toProductId")
    @Mapping(target = "productUuId", source = "uuid", qualifiedByName = "toProductUuId")
    @Mapping(target = "productBusinessUuId", source = "businessUuid", qualifiedByName = "toBusinessUuId")
    @Mapping(target = "productVersion", source = "version", qualifiedByName = "toProductVersion")
    @Mapping(target = "productStatus", source = "status", qualifiedByName = "toProductStatus")
    @Mapping(target = "manifest", source = ".", qualifiedByName = "toManifest")
    @Mapping(target = "physicalSpecs", source = ".", qualifiedByName = "toProductPhysicalSpecs")
    @Mapping(target = "productBooleans", source = ".", qualifiedByName = "toBooleans")
    @Mapping(target = "productThumbnailUrl", source = "thumbnailUrl", qualifiedByName = "toThumbnail")
    @Mapping(target = "galleryUuId", source = "galleryUuid", qualifiedByName = "toGalleryUuId")
    @Mapping(target = "variantListUuId", source = "variantListUuid", qualifiedByName = "toVariantListUuId")
    @Mapping(target = "typeListUuId", source = "typeListUuid", qualifiedByName = "toTypeListUuId")
    @Mapping(target = "priceListUuId", source = "priceListUuid", qualifiedByName = "toPriceListUuId")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    ProductAggregateRoot toAggregate(ProductEntity entity);

    @Mapping(target = "id", source = "productId.value.id")
    @Mapping(target = "uuid", source = "productUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "productBusinessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "version", source = "productVersion.value.value")
    @Mapping(target = "status", source = "productStatus.value.name")
    @Mapping(target = "name", source = "manifest.name.value.name")
    @Mapping(target = "category", source = "manifest.category.value.value")
    @Mapping(target = "description", source = "manifest.description.value.value")
    @Mapping(target = "weightAmount", source = "physicalSpecs.value.weight.amount")
    @Mapping(target = "weightUnit", source = "physicalSpecs.value.weight.weightUnit.name")
    @Mapping(target = "length", source = "physicalSpecs.value.dimensions.length")
    @Mapping(target = "width", source = "physicalSpecs.value.dimensions.width")
    @Mapping(target = "height", source = "physicalSpecs.value.dimensions.height")
    @Mapping(target = "dimensionUnit", source = "physicalSpecs.value.dimensions.sizeUnit.code")
    @Mapping(target = "careInstructions", source = "physicalSpecs.value.careInstructions.value.value")
    @Mapping(target = "archived", source = "productBooleans.archived")
    @Mapping(target = "softDeleted", source = "productBooleans.softDeleted")
    @Mapping(target = "thumbnailUrl", source = "productThumbnailUrl.value")
    @Mapping(target = "galleryUuid", source = "galleryUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "variantListUuid", source = "variantListUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "typeListUuid", source = "typeListUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "priceListUuid", source = "priceListUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    ProductEntity toEntity(ProductAggregateRoot aggregate);

    // --- Complex Composite Construction ---

    @Named("toManifest")
    default ProductManifest toManifest(ProductEntity entity) {
        return new ProductManifest(
                new ProductName(new Name(entity.getName())),
                new ProductCategory(new Category(entity.getCategory())),
                new ProductDescription(new Description(entity.getDescription()))
        );
    }

    @Named("toProductPhysicalSpecs")
    default ProductPhysicalSpecs toProductPhysicalSpecs(ProductEntity entity) {
        // Handle Null Object Pattern for empty database rows
        if (entity.getWeightAmount() == null && entity.getLength() == null) {
            return ProductPhysicalSpecs.NONE;
        }

        // 1. Reconstruct Weight using fromString()
        Weight weight = new Weight(
                entity.getWeightAmount(),
                WeightUnitEnums.fromString(entity.getWeightUnit())
        );

        // 2. Reconstruct Dimensions using fromCode()
        Dimensions dimensions = new Dimensions(
                entity.getLength(),
                entity.getWidth(),
                entity.getHeight(),
                DimensionUnitEnums.fromCode(entity.getDimensionUnit())
        );

        // 3. Reconstruct Care Instructions
        CareInstruction care = new CareInstruction(entity.getCareInstructions());

        // 4. Wrap into Domain hierarchy
        return new ProductPhysicalSpecs(new PhysicalSpecs(weight, dimensions, care));
    }


    // --- Primitive to Value Object Helpers ---

    @Named("toProductId")
    default ProductId toProductId(Long id) { return id != null ? new ProductId(PkId.of(id)) : null; }

    @Named("toProductUuId")
    default ProductUuId toProductUuId(java.util.UUID uuid) { return new ProductUuId(UuId.fromString(uuid.toString())); }

    @Named("toBusinessUuId")
    default ProductBusinessUuId toBusinessUuId(java.util.UUID uuid) { return new ProductBusinessUuId(UuId.fromString(uuid.toString())); }

    @Named("toProductVersion")
    default ProductVersion toProductVersion(Integer v) { return new ProductVersion(new Version(v)); }

    @Named("toProductStatus")
    default ProductStatus toProductStatus(String s) { return new ProductStatus(StatusEnums.valueOf(s)); }

    @Named("toThumbnail")
    default ProductThumbnailUrl toThumbnail(String url) { return new ProductThumbnailUrl(url); }

    @Named("toBooleans")
    default ProductBooleans toBooleans(ProductEntity entity) { return new ProductBooleans(entity.isArchived(), entity.isSoftDeleted()); }

    @Named("toGalleryUuId")
    default GalleryUuId toGalleryUuId(java.util.UUID uuid) { return uuid != null ? new GalleryUuId(UuId.fromString(uuid.toString())) : GalleryUuId.NONE; }

    @Named("toVariantListUuId")
    default VariantListUuId toVariantListUuId(java.util.UUID uuid) { return uuid != null ? new VariantListUuId(UuId.fromString(uuid.toString())) : VariantListUuId.NONE; }

    @Named("toTypeListUuId")
    default TypeListUuId toTypeListUuId(java.util.UUID uuid) { return uuid != null ? new TypeListUuId(UuId.fromString(uuid.toString())) : TypeListUuId.NONE; }

    @Named("toPriceListUuId")
    default PriceListUuId toPriceListUuId(java.util.UUID uuid) { return uuid != null ? new PriceListUuId(UuId.fromString(uuid.toString())) : PriceListUuId.NONE; }

    @Named("toAuditMetadata")
    default AuditMetadata toAuditMetadata(ProductEntity entity) {
        return AuditMetadata.reconstitute(
                new CreatedAt(entity.getCreatedAt()),
                new LastModified(entity.getLastModifiedAt()),
                new Actor(entity.getLastModifiedBy(), Collections.emptySet())
        );
    }

    @Named("stringToUuid")
    default java.util.UUID stringToUuid(String value) { return value != null ? java.util.UUID.fromString(value) : null; }
}

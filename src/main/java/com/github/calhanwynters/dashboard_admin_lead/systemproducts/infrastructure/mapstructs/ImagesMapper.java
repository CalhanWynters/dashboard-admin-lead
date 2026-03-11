package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.ProductBooleansLEGACY;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.ImagesEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Collections;
import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.images.ImagesDomainWrapper.*;

@Mapper(componentModel = "spring")
public interface ImagesMapper {

    @Mapping(target = "imageId", source = "id", qualifiedByName = "toImageId")
    @Mapping(target = "imagesUuId", source = "uuid", qualifiedByName = "toImageUuId")
    @Mapping(target = "imagesBusinessUuId", source = "businessUuid", qualifiedByName = "toBusinessUuId")
    @Mapping(target = "imageName", source = "name", qualifiedByName = "toImageName")
    @Mapping(target = "imageDescription", source = "description", qualifiedByName = "toImageDescription")
    @Mapping(target = "imageUrl", source = "url", qualifiedByName = "toImageUrl")
    @Mapping(target = "productBooleans", source = ".", qualifiedByName = "toProductBooleans")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    ImageAggregateLEGACY toAggregate(ImagesEntity entity);

    @Mapping(target = "id", source = "imageId.value.id")
    @Mapping(target = "uuid", source = "imagesUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "imagesBusinessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "name", source = "imageName.value.name")
    @Mapping(target = "description", source = "imageDescription.value.value") // Unwrapping Description record
    @Mapping(target = "url", source = "imageUrl.value") // ImageUrl(String value)
    @Mapping(target = "archived", source = "productBooleans.archived")
    @Mapping(target = "softDeleted", source = "productBooleans.softDeleted")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    ImagesEntity toEntity(ImageAggregateLEGACY aggregate);

    // --- MAPPING HELPERS ---

    @Named("toImageId")
    default ImageId toImageId(Long id) { return id != null ? new ImageId(PkId.of(id)) : null; }

    @Named("toImageUuId")
    default ImageUuId toImageUuId(java.util.UUID uuid) {
        return uuid != null ? new ImageUuId(UuId.fromString(uuid.toString())) : null;
    }

    @Named("toBusinessUuId")
    default ImagesBusinessUuId toBusinessUuId(java.util.UUID uuid) {
        return uuid != null ? new ImagesBusinessUuId(UuId.fromString(uuid.toString())) : null;
    }

    @Named("toImageName")
    default ImageName toImageName(String name) { return name != null ? new ImageName(new Name(name)) : null; }

    @Named("toImageDescription")
    default ImageDescription toImageDescription(String desc) {
        return desc != null ? new ImageDescription(new Description(desc)) : null;
    }

    @Named("toImageUrl")
    default ImagesDomainWrapper.ImageUrl toImageUrl(String url) {
        return url != null ? new ImagesDomainWrapper.ImageUrl(url) : null;
    }

    @Named("toProductBooleans")
    default ProductBooleansLEGACY toProductBooleans(ImagesEntity entity) {
        return new ProductBooleansLEGACY(entity.isArchived(), entity.isSoftDeleted());
    }

    @Named("toAuditMetadata")
    default AuditMetadata toAuditMetadata(ImagesEntity entity) {
        return AuditMetadata.reconstitute(
                new CreatedAt(entity.getCreatedAt()),
                new LastModified(entity.getLastModifiedAt()),
                new Actor(entity.getLastModifiedBy(), Collections.emptySet())
        );
    }

    @Named("stringToUuid")
    default java.util.UUID stringToUuid(String value) {
        return value != null ? java.util.UUID.fromString(value) : null;
    }
}

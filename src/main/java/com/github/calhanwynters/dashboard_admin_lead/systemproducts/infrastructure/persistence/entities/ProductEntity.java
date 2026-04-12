package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "system_products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "business_uuid", nullable = false)
    private UUID businessUuid;

    @Column(name = "version_count", nullable = false)
    private Integer version;

    @Column(name = "status_code", nullable = false)
    private String status;

    @Column(name = "region_code", nullable = false)
    private String region;

    // --- Flattened Manifest ---
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category_value", nullable = false)
    private String category;

    @Column(name = "description_text", columnDefinition = "TEXT")
    private String description;

    // --- Flattened Physical Specs ---
    @Column(name = "weight_amount", precision = 19, scale = 5)
    private BigDecimal weightAmount;

    @Column(name = "weight_unit")
    private String weightUnit;

    @Column(name = "dim_length", precision = 19, scale = 10)
    private BigDecimal length;

    @Column(name = "dim_width", precision = 19, scale = 10)
    private BigDecimal width;

    @Column(name = "dim_height", precision = 19, scale = 10)
    private BigDecimal height;

    @Column(name = "dim_unit")
    private String dimensionUnit;

    @Column(name = "care_instructions", columnDefinition = "TEXT")
    private String careInstructions;

    // --- Booleans & Metadata ---
    @Column(name = "is_archived", nullable = false)
    private boolean archived;

    @Column(name = "is_soft_deleted", nullable = false)
    private boolean softDeleted;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    // --- Cross-Aggregate UUIDs ---
    @Column(name = "gallery_uuid")
    private UUID galleryUuid;

    @Column(name = "variant_list_uuid")
    private UUID variantListUuid;

    @Column(name = "type_list_uuid")
    private UUID typeListUuid;

    @Column(name = "price_list_uuid")
    private UUID priceListUuid;

    // --- Audit Fields ---
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "last_modified_at", nullable = false)
    private OffsetDateTime lastModifiedAt;

    @Column(name = "last_modified_by", nullable = false)
    private String lastModifiedBy;

    public ProductEntity() {}

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getUuid() { return uuid; }
    public void setUuid(UUID uuid) { this.uuid = uuid; }

    public UUID getBusinessUuid() { return businessUuid; }
    public void setBusinessUuid(UUID businessUuid) { this.businessUuid = businessUuid; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRegion() { return region; }
    public void setRegion() { this.region = region;}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getWeightAmount() { return weightAmount; }
    public void setWeightAmount(BigDecimal weightAmount) { this.weightAmount = weightAmount; }

    public String getWeightUnit() { return weightUnit; }
    public void setWeightUnit(String weightUnit) { this.weightUnit = weightUnit; }

    public BigDecimal getLength() { return length; }
    public void setLength(BigDecimal length) { this.length = length; }

    public BigDecimal getWidth() { return width; }
    public void setWidth(BigDecimal width) { this.width = width; }

    public BigDecimal getHeight() { return height; }
    public void setHeight(BigDecimal height) { this.height = height; }

    public String getDimensionUnit() { return dimensionUnit; }
    public void setDimensionUnit(String dimensionUnit) { this.dimensionUnit = dimensionUnit; }

    public String getCareInstructions() { return careInstructions; }
    public void setCareInstructions(String careInstructions) { this.careInstructions = careInstructions; }

    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }

    public boolean isSoftDeleted() { return softDeleted; }
    public void setSoftDeleted(boolean softDeleted) { this.softDeleted = softDeleted; }

    public String getThumbnailUrl() { return thumbnailUrl; }
    public void setThumbnailUrl(String thumbnailUrl) { this.thumbnailUrl = thumbnailUrl; }

    public UUID getGalleryUuid() { return galleryUuid; }
    public void setGalleryUuid(UUID galleryUuid) { this.galleryUuid = galleryUuid; }

    public UUID getVariantListUuid() { return variantListUuid; }
    public void setVariantListUuid(UUID variantListUuid) { this.variantListUuid = variantListUuid; }

    public UUID getTypeListUuid() { return typeListUuid; }
    public void setTypeListUuid(UUID typeListUuid) { this.typeListUuid = typeListUuid; }

    public UUID getPriceListUuid() { return priceListUuid; }
    public void setPriceListUuid(UUID priceListUuid) { this.priceListUuid = priceListUuid; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getLastModifiedAt() { return lastModifiedAt; }
    public void setLastModifiedAt(OffsetDateTime lastModifiedAt) { this.lastModifiedAt = lastModifiedAt; }

    public String getLastModifiedBy() { return lastModifiedBy; }
    public void setLastModifiedBy(String lastModifiedBy) { this.lastModifiedBy = lastModifiedBy; }
}

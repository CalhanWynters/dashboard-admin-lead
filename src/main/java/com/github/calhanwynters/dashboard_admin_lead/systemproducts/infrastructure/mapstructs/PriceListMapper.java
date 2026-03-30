package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.calhanwynters.dashboard_admin_lead.common.*;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.PriceEntryEmbeddable;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.PriceListEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import com.fasterxml.jackson.core.type.TypeReference;


import static com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;

import java.util.*;

@Mapper(componentModel = "spring")
public abstract class PriceListMapper {

    protected final ObjectMapper jsonMapper;

    // Internal registry: Links the Domain Enum to the Concrete Value Object Class
    private static final Map<PricingStrategyType, Class<? extends PurchasePricing>> STRATEGY_MAP = Map.ofEntries(
            Map.entry(PricingStrategyType.FIXED, PriceFixedPurchase.class),
            Map.entry(PricingStrategyType.NONE, PriceNonePurchase.class),
            Map.entry(PricingStrategyType.FRACT_TIERED_GRAD, PriceFractTieredGradPurchase.class),
            Map.entry(PricingStrategyType.FRACT_SCALED, PriceFractScaledPurchase.class),
            Map.entry(PricingStrategyType.FRACT_TIERED_VOL, PriceFractTieredVolPurchase.class),
            Map.entry(PricingStrategyType.INT_SCALED, PriceIntScaledPurchase.class),
            Map.entry(PricingStrategyType.INT_TIERED_GRAD, PriceIntTieredGradPurchase.class),
            Map.entry(PricingStrategyType.INT_TIERED_VOL, PriceIntTieredVolPurchase.class)
    );

    public PriceListMapper(ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }



    @Mapping(target = "id", source = "id", qualifiedByName = "toPriceListId")
    @Mapping(target = "uuId", source = "uuid", qualifiedByName = "toPriceListUuId")
    @Mapping(target = "businessUuId", source = "businessUuid", qualifiedByName = "toBusinessUuId")
    @Mapping(target = "strategyBoundary", source = "strategySlug", qualifiedByName = "slugToEnum")
    @Mapping(target = "priceListVersion", source = "version", qualifiedByName = "toVersion")
    @Mapping(target = "isActive", source = "active") // Maps boolean isActive
    @Mapping(target = "prices", source = "prices", qualifiedByName = "toPricingMap")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    @Mapping(target = "lifecycleState", source = "lifecycleState") // Direct mapping if types match
    @Mapping(target = "optLockVer", source = "version") // Example: using version for optimistic locking
    @Mapping(target = "schemaVer", constant = "1") // Defaulting schema version
    @Mapping(target = "lastSyncedAt", source = "lastModifiedAt")
    public abstract PriceListAggregate toAggregate(PriceListEntity entity);

    @Mapping(target = "id", source = "id.value.value")
    @Mapping(target = "uuid", source = "uuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "businessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "strategySlug", source = "strategyBoundary", qualifiedByName = "enumToSlug")
    @Mapping(target = "prices", source = "multiCurrencyPrices", qualifiedByName = "fromPricingMap")
    @Mapping(target = "version", source = "priceListVersion.value.value")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    public abstract PriceListEntity toEntity(PriceListAggregate aggregate);



    // --- Identity & Version Helpers ---

    @Named("toPriceListId")
    protected PriceListId toPriceListId(Long id) {
        return id == null ? null : PriceListId.of(id);
    }

    @Named("toPriceListUuId")
    protected PriceListUuId toPriceListUuId(UUID uuid) {
        return uuid == null ? null : new PriceListDomainWrapper.PriceListUuId(new UuId(uuid.toString()));
    }

    @Named("toBusinessUuId")
    protected PriceListBusinessUuId toBusinessUuId(UUID uuid) {
        return uuid == null ? null : new PriceListBusinessUuId(new UuId(uuid.toString()));
    }

    @Named("toVersion")
    protected PriceListVersion toVersion(Integer version) {
        return version == null ? null : new PriceListVersion(new Version(version));
    }

    @Named("stringToUuid")
    protected UUID stringToUuid(String value) {
        return value == null ? null : UUID.fromString(value);
    }

    // --- Strategy & JSON Helpers ---

    @Named("slugToEnum")
    protected PricingStrategyType slugToEnum(String slug) {
        return slug == null ? null : PricingStrategyType.valueOf(slug);
    }

    @Named("enumToSlug")
    protected String enumToSlug(PricingStrategyType type) {
        return type == null ? null : type.name();
    }

    @Named("toPricingMap")
    protected Map<UuId, Map<Currency, PurchasePricing>> toPricingMap(List<PriceEntryEmbeddable> entries) {
        Map<UuId, Map<Currency, PurchasePricing>> rootMap = new HashMap<>();
        if (entries == null) return rootMap;

        for (var entry : entries) {
            UuId itemKey = new UuId(entry.getItemId().toString());
            Currency currency = Currency.getInstance(entry.getCurrencyCode());

            PricingStrategyType strategyType = PricingStrategyType.valueOf(entry.getPricingType());
            Class<? extends PurchasePricing> concreteClass = STRATEGY_MAP.get(strategyType);

            PurchasePricing pricing = jsonMapper.convertValue(entry.getStrategyDetails(), concreteClass);
            rootMap.computeIfAbsent(itemKey, k -> new HashMap<>()).put(currency, pricing);
        }
        return rootMap;
    }

    @Named("fromPricingMap")
    protected List<PriceEntryEmbeddable> fromPricingMap(Map<UuId, Map<Currency, PurchasePricing>> map) {
        List<PriceEntryEmbeddable> flatList = new ArrayList<>();
        if (map == null) return flatList;

        map.forEach((itemId, currencyMap) -> currencyMap.forEach((currency, pricing) -> {
            PriceEntryEmbeddable entry = new PriceEntryEmbeddable();
            entry.setItemId(UUID.fromString(itemId.value()));
            entry.setCurrencyCode(currency.getCurrencyCode());

            String slug = STRATEGY_MAP.entrySet().stream()
                    .filter(e -> e.getValue().isInstance(pricing))
                    .map(e -> e.getKey().name())
                    .findFirst()
                    .orElse("UNKNOWN");

            entry.setPricingType(slug);
            entry.setStrategyDetails(jsonMapper.convertValue(pricing, new TypeReference<>() {
            }));
            flatList.add(entry);
        }));
        return flatList;
    }

    @Named("toAuditMetadata")
    protected AuditMetadata toAuditMetadata(PriceListEntity entity) {
        return AuditMetadata.reconstitute(
                new CreatedAt(entity.getCreatedAt()),
                new LastModified(entity.getLastModifiedAt()),
                new Actor(entity.getLastModifiedBy(), Collections.emptySet())
        );
    }
}


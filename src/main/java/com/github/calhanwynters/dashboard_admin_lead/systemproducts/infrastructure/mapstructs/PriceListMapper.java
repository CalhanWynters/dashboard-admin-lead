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

    @Mapping(target = "priceListId", source = "id", qualifiedByName = "toPriceListId")
    @Mapping(target = "priceListUuId", source = "uuid", qualifiedByName = "toPriceListUuId")
    @Mapping(target = "priceListBusinessUuId", source = "businessUuid", qualifiedByName = "toBusinessUuId")
    @Mapping(target = "strategyBoundary", source = "strategySlug", qualifiedByName = "slugToEnum")
    @Mapping(target = "multiCurrencyPrices", source = "prices", qualifiedByName = "toPricingMap")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    @Mapping(target = "priceListVersion", source = "version", qualifiedByName = "toVersion")
    public abstract PriceListAggregateLEGACY toAggregate(PriceListEntity entity);

    @Mapping(target = "id", source = "priceListId.value.value")
    @Mapping(target = "uuid", source = "priceListUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "priceListBusinessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "strategySlug", source = "strategyBoundary", qualifiedByName = "enumToSlug")
    @Mapping(target = "prices", source = "multiCurrencyPrices", qualifiedByName = "fromPricingMap")
    @Mapping(target = "version", source = "priceListVersion.value.value")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    public abstract PriceListEntity toEntity(PriceListAggregateLEGACY aggregate);


    // --- Identity & Version Helpers ---

    @Named("toPriceListId")
    protected PriceListDomainWrapper.PriceListId toPriceListId(Long id) {
        return id == null ? null : PriceListDomainWrapper.PriceListId.of(id);
    }

    @Named("toPriceListUuId")
    protected PriceListDomainWrapper.PriceListUuId toPriceListUuId(UUID uuid) {
        return new PriceListDomainWrapper.PriceListUuId(new UuId(uuid.toString()));
    }

    @Named("toBusinessUuId")
    protected PriceListDomainWrapper.PriceListBusinessUuId toBusinessUuId(UUID uuid) {
        return new PriceListDomainWrapper.PriceListBusinessUuId(new UuId(uuid.toString()));
    }

    @Named("toVersion")
    protected PriceListDomainWrapper.PriceListVersion toVersion(Integer version) {
        return new PriceListDomainWrapper.PriceListVersion(new Version(version));
    }

    @Named("stringToUuid")
    protected UUID stringToUuid(String value) {
        return value == null ? null : UUID.fromString(value);
    }

    // --- Strategy & JSON Helpers ---

    @Named("slugToEnum") // Changed from slugToClass
    protected PricingStrategyType slugToEnum(String slug) {
        return slug == null ? null : PricingStrategyType.valueOf(slug);
    }

    @Named("enumToSlug") // Changed from classToSlug
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

            // Use the Enum to find the concrete class for Jackson
            PricingStrategyType strategyType = PricingStrategyType.valueOf(entry.getPricingType());
            Class<? extends PurchasePricing> concreteClass = STRATEGY_MAP.get(strategyType);

            PurchasePricing pricing = jsonMapper.convertValue(
                    entry.getStrategyDetails(),
                    concreteClass
            );

            rootMap.computeIfAbsent(itemKey, k -> new HashMap<>()).put(currency, pricing);
        }
        return rootMap;
    }

    @Named("fromPricingMap")
    protected List<PriceEntryEmbeddable> fromPricingMap(Map<UuId, Map<Currency, PurchasePricing>> map) {
        List<PriceEntryEmbeddable> flatList = new ArrayList<>();
        if (map == null) return flatList;

        TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {};

        map.forEach((itemId, currencyMap) -> currencyMap.forEach((currency, pricing) -> {
            PriceEntryEmbeddable entry = new PriceEntryEmbeddable();
            entry.setItemId(UUID.fromString(itemId.value()));
            entry.setCurrencyCode(currency.getCurrencyCode());

            // Helper logic to find the Enum name for the DB slug
            String slug = STRATEGY_MAP.entrySet().stream()
                    .filter(e -> e.getValue().isInstance(pricing))
                    .map(e -> e.getKey().name())
                    .findFirst()
                    .orElse("UNKNOWN");

            entry.setPricingType(slug);
            entry.setStrategyDetails(jsonMapper.convertValue(pricing, typeRef));
            flatList.add(entry);
        }));
        return flatList;
    }

    // --- Audit Helper ---

    @Named("toAuditMetadata")
    protected AuditMetadata toAuditMetadata(PriceListEntity entity) {
        return AuditMetadata.reconstitute(
                new CreatedAt(entity.getCreatedAt()),
                new LastModified(entity.getLastModifiedAt()),
                // Pass an empty set for roles, as the audit trail identifies 'who',
                // but roles are usually transient session data.
                new Actor(entity.getLastModifiedBy(), Collections.emptySet())
        );
    }


}

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

    private static final Map<String, Class<? extends PurchasePricing>> STRATEGY_REGISTRY = Map.of(
            "SIMPLE", SimplePurchasePricing.class,
            "FIXED", PriceFixedPurchase.class,
            "TIERED_GRAD", PriceFractTieredGradPurchase.class
    );

    public PriceListMapper(ObjectMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Mapping(target = "priceListId", source = "id", qualifiedByName = "toPriceListId")
    @Mapping(target = "priceListUuId", source = "uuid", qualifiedByName = "toPriceListUuId")
    @Mapping(target = "priceListBusinessUuId", source = "businessUuid", qualifiedByName = "toBusinessUuId")
    @Mapping(target = "strategyBoundary", source = "strategySlug", qualifiedByName = "slugToClass")
    @Mapping(target = "multiCurrencyPrices", source = "prices", qualifiedByName = "toPricingMap")
    @Mapping(target = "auditMetadata", source = ".", qualifiedByName = "toAuditMetadata")
    @Mapping(target = "priceListVersion", source = "version", qualifiedByName = "toVersion")
    public abstract PriceListAggregate toAggregate(PriceListEntity entity);

    @Mapping(target = "id", source = "priceListId.value.value")
    @Mapping(target = "uuid", source = "priceListUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "businessUuid", source = "priceListBusinessUuId.value.value", qualifiedByName = "stringToUuid")
    @Mapping(target = "strategySlug", source = "strategyBoundary", qualifiedByName = "classToSlug")
    @Mapping(target = "prices", source = "multiCurrencyPrices", qualifiedByName = "fromPricingMap")
    @Mapping(target = "version", source = "priceListVersion.value.value")
    @Mapping(target = "createdAt", source = "auditMetadata.createdAt.value")
    @Mapping(target = "lastModifiedAt", source = "auditMetadata.lastModified.value")
    @Mapping(target = "lastModifiedBy", source = "auditMetadata.lastModifiedBy.identity")
    public abstract PriceListEntity toEntity(PriceListAggregate aggregate);


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

    @Named("slugToClass")
    protected Class<? extends PurchasePricing> slugToClass(String slug) {
        return STRATEGY_REGISTRY.getOrDefault(slug, PurchasePricing.class);
    }

    @Named("classToSlug")
    protected String classToSlug(Class<?> clazz) {
        return STRATEGY_REGISTRY.entrySet().stream()
                .filter(e -> e.getValue().isAssignableFrom(clazz))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("UNKNOWN");
    }

    @Named("toPricingMap")
    protected Map<UuId, Map<Currency, PurchasePricing>> toPricingMap(List<PriceEntryEmbeddable> entries) {
        Map<UuId, Map<Currency, PurchasePricing>> rootMap = new HashMap<>();
        for (var entry : entries) {
            UuId itemKey = new UuId(entry.getItemId().toString());
            Currency currency = Currency.getInstance(entry.getCurrencyCode());

            PurchasePricing pricing = jsonMapper.convertValue(
                    entry.getStrategyDetails(),
                    getConcreteClass(entry.getPricingType())
            );

            rootMap.computeIfAbsent(itemKey, k -> new HashMap<>()).put(currency, pricing);
        }
        return rootMap;
    }

    @Named("fromPricingMap")
    protected List<PriceEntryEmbeddable> fromPricingMap(Map<UuId, Map<Currency, PurchasePricing>> map) {
        List<PriceEntryEmbeddable> flatList = new ArrayList<>();

        // Define a type reference for Map<String, Object> to satisfy the compiler
        TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {};

        map.forEach((itemId, currencyMap) -> currencyMap.forEach((currency, pricing) -> {
            PriceEntryEmbeddable entry = new PriceEntryEmbeddable();
            entry.setItemId(UUID.fromString(itemId.value()));
            entry.setCurrencyCode(currency.getCurrencyCode());
            entry.setPricingType(classToSlug(pricing.getClass()));

            // Use the TypeReference to perform a type-safe conversion
            entry.setStrategyDetails(jsonMapper.convertValue(pricing, typeRef));

            flatList.add(entry);
        }));
        return flatList;
    }

    private Class<? extends PurchasePricing> getConcreteClass(String type) {
        return STRATEGY_REGISTRY.get(type);
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

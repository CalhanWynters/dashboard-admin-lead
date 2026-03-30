package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListAggregate;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PricingStrategyType;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PurchasePricing;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.PriceListEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.processing.Generated;
import java.util.Currency;
import java.util.Map;
import java.util.UUID;

@Generated(value = "org.mapstruct.ap.MappingProcessor")
@Component
public class PriceListMapperImpl extends PriceListMapper {

    @Autowired
    public PriceListMapperImpl(ObjectMapper jsonMapper) {
        super(jsonMapper);
    }

    @Override
    public PriceListAggregate toAggregate(PriceListEntity entity) {
        if (entity == null) return null;

        // Use the protected helper methods from the abstract class
        PriceListId id = toPriceListId(entity.getId());
        PriceListUuId uuId = toPriceListUuId(entity.getUuid());
        PriceListBusinessUuId businessUuId = toBusinessUuId(entity.getBusinessUuid());
        PricingStrategyType strategyBoundary = slugToEnum(entity.getStrategySlug());
        PriceListVersion version = toVersion(entity.getVersion());
        Map<UuId, Map<Currency, PurchasePricing>> prices = toPricingMap(entity.getPrices());
        AuditMetadata auditMetadata = toAuditMetadata(entity);

        // This matches the exact constructor in your updated Aggregate code
        return new PriceListAggregate(
                id,
                uuId,
                businessUuId,
                strategyBoundary,
                version,
                entity.isActive(),
                prices,
                auditMetadata,
                null, // lifecycleState (Set to null or add field to Entity if missing)
                entity.getVersion() != null ? entity.getVersion().longValue() : 0L, // optLockVer
                1,    // schemaVer
                entity.getLastModifiedAt() // lastSyncedAt
        );
    }

    @Override
    public PriceListEntity toEntity(PriceListAggregate aggregate) {
        if (aggregate == null) return null;

        PriceListEntity entity = new PriceListEntity();

        // Use the getters defined in BaseAggregateRoot and PriceListAggregate
        if (aggregate.getId() != null) {
            entity.setId(aggregate.getId().value().value());
        }

        entity.setUuid(UUID.fromString(aggregate.getUuId().value().value()));
        entity.setBusinessUuid(UUID.fromString(aggregate.getBusinessUuId().value().value()));
        entity.setStrategySlug(enumToSlug(aggregate.getStrategyBoundary()));
        entity.setActive(aggregate.isActive());

        // Map the prices back to the DB format
        entity.setPrices(fromPricingMap(aggregate.getMultiCurrencyPrices()));

        // Audit & Version mapping
        entity.setVersion(aggregate.getPriceListVersion().value().value());
        entity.setCreatedAt(aggregate.getAuditMetadata().createdAt().value());
        entity.setLastModifiedAt(aggregate.getAuditMetadata().lastModified().value());
        entity.setLastModifiedBy(aggregate.getAuditMetadata().lastModifiedBy().identity());

        return entity;
    }
}
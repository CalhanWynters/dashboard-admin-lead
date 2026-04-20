package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.LifecycleState;
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
public class PriceListMapStructImpl extends PriceListMapStruct {

    @Autowired
    public PriceListMapStructImpl(ObjectMapper jsonMapper) {
        super(jsonMapper);
    }

    @Override
    public PriceListAggregate toAggregate(PriceListEntity entity) {
        LifecycleState lifecycleState = new LifecycleState(entity.isArchived(), entity.isSoftDeleted());

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
                lifecycleState,
                entity.getVersion() != null ? entity.getVersion().longValue() : 0L, // optLockVer
                1,    // schemaVer
                entity.getLastModifiedAt() // lastSyncedAt
        );
    }

    @Override
    public PriceListEntity toEntity(PriceListAggregate aggregate) {
        if (aggregate == null) return null;

        PriceListEntity entity = new PriceListEntity();

        // 1. PK Mapping (Using .value().value() for nested wrapper)
        if (aggregate.getId() != null && aggregate.getId().value() != null) {
            entity.setId(aggregate.getId().value().value());
        }

        // 2. UUID & Strategy Mapping
        entity.setUuid(UUID.fromString(aggregate.getUuId().value().value()));
        entity.setBusinessUuid(UUID.fromString(aggregate.getBusinessUuId().value().value()));
        entity.setStrategySlug(enumToSlug(aggregate.getStrategyBoundary()));

        // Matches the boolean field in your Aggregate
        entity.setActive(aggregate.isActive());

        // 3. Price Map Mapping
        entity.setPrices(fromPricingMap(aggregate.getMultiCurrencyPrices()));

        // 4. Audit & Versioning
        if (aggregate.getAuditMetadata() != null) {
            entity.setCreatedAt(aggregate.getAuditMetadata().createdAt().value());
            entity.setLastModifiedAt(aggregate.getAuditMetadata().lastModified().value());
            entity.setLastModifiedBy(aggregate.getAuditMetadata().lastModifiedBy().identity());
        }

        if (aggregate.getPriceListVersion() != null) {
            entity.setVersion(aggregate.getPriceListVersion().value().value());
        }

        // 5. Lifecycle Mapping (Record Accessors: archived() and softDeleted())
        if (aggregate.getLifecycleState() != null) {
            entity.setArchived(aggregate.getLifecycleState().archived());
            entity.setSoftDeleted(aggregate.getLifecycleState().softDeleted());
        }

        return entity;
    }

}
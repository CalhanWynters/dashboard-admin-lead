package com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.mapstructs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.calhanwynters.dashboard_admin_lead.common.UuId;
import com.github.calhanwynters.dashboard_admin_lead.common.compositeclasses.AuditMetadata;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListAggregateLEGACY;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.PriceListDomainWrapper.*;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PricingStrategyType;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.core.domain.aggregates.pricelist.purchasepricingmodel.PurchasePricing;
import com.github.calhanwynters.dashboard_admin_lead.systemproducts.infrastructure.persistence.entities.PriceListEntity;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Currency;
import java.util.Map;

@Generated(
        value = "org.mapstruct.ap.MappingProcessor"
)
@Component // Spring picks this up automatically
public class PriceListMapperImpl extends PriceListMapper {

    @Autowired // MapStruct uses your constructor to inject the ObjectMapper
    public PriceListMapperImpl(ObjectMapper jsonMapper) {
        super( jsonMapper );
    }

    @Override
    public PriceListAggregateLEGACY toAggregate(PriceListEntity entity) {
        if ( entity == null ) {
            return null;
        }

        // MapStruct calls your @Named methods automatically
        PriceListId priceListId = toPriceListId( entity.getId() );
        PriceListUuId priceListUuId = toPriceListUuId( entity.getUuid() );
        PriceListBusinessUuId priceListBusinessUuId = toBusinessUuId( entity.getBusinessUuid() );
        PricingStrategyType strategyBoundary = slugToEnum( entity.getStrategySlug() );
        Map<UuId, Map<Currency, PurchasePricing>> multiCurrencyPrices = toPricingMap( entity.getPrices() );
        AuditMetadata auditMetadata = toAuditMetadata( entity );
        PriceListVersion priceListVersion = toVersion( entity.getVersion() );

        // MapStruct calls your Aggregate constructor
        PriceListAggregateLEGACY priceListAggregate = new PriceListAggregateLEGACY(
                priceListId,
                priceListUuId,
                strategyBoundary,
                priceListBusinessUuId,
                priceListVersion,
                entity.isActive(),
                null, // You might need a helper for ProductBooleans
                auditMetadata,
                multiCurrencyPrices
        );

        return priceListAggregate;
    }

    @Override
    public PriceListEntity toEntity(PriceListAggregateLEGACY aggregate) {
        if ( aggregate == null ) {
            return null;
        }

        PriceListEntity priceListEntity = new PriceListEntity();

        // Navigates your Domain Record paths (.value().id())
        priceListEntity.setId( aggregate.getPriceListId().value().value() );
        priceListEntity.setUuid( stringToUuid( aggregate.getPriceListUuId().value().value() ) );
        priceListEntity.setBusinessUuid( stringToUuid( aggregate.getPriceListBusinessUuId().value().value() ) );

        // Calls your slug logic
        priceListEntity.setStrategySlug( enumToSlug( aggregate.getStrategyBoundary() ) );

        // Calls your JSONB/Jackson logic
        priceListEntity.setPrices( fromPricingMap( aggregate.getMultiCurrencyPrices() ) );

        // Maps the Audit fields from your records
        priceListEntity.setVersion( aggregate.getPriceListVersion().value().value() );
        priceListEntity.setCreatedAt( aggregate.getAuditMetadata().createdAt().value() );
        priceListEntity.setLastModifiedAt( aggregate.getAuditMetadata().lastModified().value() );
        priceListEntity.setLastModifiedBy( aggregate.getAuditMetadata().lastModifiedBy().identity() );

        return priceListEntity;
    }
}
